(ns app.components.game-component
  (:require [app.components.cards.card-component :refer [card-component]]
            [app.components.cards.hand :refer [hand-cards]]
            [app.components.cards.table :refer [table-cards]]
            [app.components.decks.decks-section :refer [decks-section]]
            [app.components.modal.card-options :as card.option]
            [app.i18n]
            [app.logic.game :refer [discard-action play-action undo-play-action]]
            [helix.core :refer [$ defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]))

;; ── Math utilities (ported from Swapy math.ts) ───────────────────────────────

(defn- lerp [a b t] (+ a (* (- b a) t)))
(defn- clamp [v mn mx] (min (max v mn) mx))
(defn- remap [a b c d v] (lerp c d (clamp (/ (- v a) (- b a)) 0 1)))

;; ── Zone detection ────────────────────────────────────────────────────────────

(defn- point-in-rect? [x y rect]
  (and rect
       (>= x (.-left rect))
       (<= x (.-right rect))
       (>= y (.-top rect))
       (<= y (.-bottom rect))))

(defn- detect-over-zone [x y table-ref discard-ref hand-ref]
  (let [table-el   (.-current table-ref)
        discard-el (.-current discard-ref)
        hand-el    (.-current hand-ref)]
    (cond
      (and table-el   (point-in-rect? x y (.getBoundingClientRect table-el)))   :table
      (and discard-el (point-in-rect? x y (.getBoundingClientRect discard-el))) :discard
      (and hand-el    (point-in-rect? x y (.getBoundingClientRect hand-el)))    :hand
      :else nil)))

;; ── FLIP animation (ported from Swapy flip.ts) ───────────────────────────────
;; Captures bounding rects BEFORE a DOM mutation, then animates the delta AFTER.

(defn- capture-rects! [zone-ref]
  (when-let [el (.-current zone-ref)]
    (let [cards (.querySelectorAll el "[data-card-id]")]
      (into {} (map (fn [card]
                      [(.. card -dataset -cardId)
                       (.getBoundingClientRect card)])
                    (array-seq cards))))))

(defn- flip-animate! [zone-ref initial-rects]
  (js/requestAnimationFrame
    (fn []
      (when-let [el (.-current zone-ref)]
        (doseq [card (array-seq (.querySelectorAll el "[data-card-id]"))]
          (let [card-id (.. card -dataset -cardId)
                init    (get initial-rects card-id)
                final   (.getBoundingClientRect card)]
            (when init
              (let [dx (- (.-left init) (.-left final))
                    dy (- (.-top init)  (.-top final))]
                (when (or (not= dx 0) (not= dy 0))
                  ;; Invert: jump element back to its initial position
                  (set! (.. card -style -transition) "none")
                  (set! (.. card -style -transform)
                        (str "translate(" dx "px," dy "px)"))
                  ;; Play: animate back to final position on next frame
                  (js/requestAnimationFrame
                    (fn []
                      (set! (.. card -style -transition)
                            "transform 0.35s cubic-bezier(0.34, 1.56, 0.64, 1)")
                      (set! (.. card -style -transform) "none"))))))))))))

;; ── Lerp loop (ported from Swapy math.ts) ────────────────────────────────────
;; Runs via requestAnimationFrame; smoothly moves ghost card toward cursor.

(defn- run-lerp-loop! [ctx]
  (let [{:keys [lerp-running lerp-raf lerp-x lerp-y target-x target-y set-drag-state]} ctx
        lx (lerp (.-current lerp-x) (.-current target-x) 0.15)
        ly (lerp (.-current lerp-y) (.-current target-y) 0.15)]
    (set! (.-current lerp-x) lx)
    (set! (.-current lerp-y) ly)
    (set-drag-state #(assoc % :x lx :y ly))
    (when (.-current lerp-running)
      (set! (.-current lerp-raf)
            (js/requestAnimationFrame #(run-lerp-loop! ctx))))))

;; ── Component ─────────────────────────────────────────────────────────────────

(defnc game-component [{{:keys [hand table] :as game-state} :game-state
                        :keys [set-game-state set-modal-state]}]
  (let [mobile-device? (or (< 0 (.-maxTouchPoints js/navigator))
                           (boolean (re-find #"Mobi|Android|iPhone|iPad|iPod"
                                             (or (.-userAgent js/navigator) ""))))
        lock-scroll!   (fn []
                         (when mobile-device?
                           (.add (.-classList (.-documentElement js/document)) "drag-scroll-lock")
                           (.add (.-classList (.-body js/document)) "drag-scroll-lock")))
        unlock-scroll! (fn []
                         (.remove (.-classList (.-documentElement js/document)) "drag-scroll-lock")
                         (.remove (.-classList (.-body js/document)) "drag-scroll-lock"))

        ;; Drag state
        [drag-state set-drag-state]
        (hooks/use-state {:dragging?  false
                          :holding?   false ; true during 150ms hold delay
                          :card       nil
                          :source     nil
                          :start-x    0
                          :start-y    0
                          :x          0
                          :y          0
                          :hover-zone nil}) ; zone currently under cursor

        ;; Zone refs
        table-ref   (hooks/use-ref nil)
        discard-ref (hooks/use-ref nil)
        hand-ref    (hooks/use-ref nil)

        ;; Interaction refs
        suppress-next-click? (hooks/use-ref false)
        hold-timeout         (hooks/use-ref nil)

        ;; Lerp refs (RAF-based smooth ghost card movement)
        lerp-running (hooks/use-ref false)
        lerp-raf     (hooks/use-ref nil)
        lerp-x       (hooks/use-ref 0) ; current interpolated position
        lerp-y       (hooks/use-ref 0)
        target-x     (hooks/use-ref 0) ; raw cursor position
        target-y     (hooks/use-ref 0)

        ;; pointer-active? is true during both holding and dragging phases.
        ;; Using a single derived bool as the effect dependency means the
        ;; event listeners stay attached during the holding → dragging transition
        ;; (instead of being torn down and re-added).
        pointer-active? (or (:dragging? drag-state) (:holding? drag-state))

        on-pointer-down
        (fn [card source e]
          (.preventDefault e)
          (let [cx (.-clientX e)
                cy (.-clientY e)]
            ;; Prime lerp refs so the ghost starts exactly at the pointer
            (set! (.-current lerp-x) cx)
            (set! (.-current lerp-y) cy)
            (set! (.-current target-x) cx)
            (set! (.-current target-y) cy)
            ;; Enter holding state (shows pulse, starts listeners via effect)
            (set-drag-state {:holding?   true
                             :dragging?  false
                             :card       card
                             :source     source
                             :start-x    cx
                             :start-y    cy
                             :x          cx
                             :y          cy
                             :hover-zone nil})
            ;; Schedule drag start after 150ms (dragOnHold — prevents accidental drags)
            (set! (.-current hold-timeout)
                  (js/setTimeout
                    (fn []
                      (set! (.-current hold-timeout) nil)
                      ;; Re-sync lerp start in case cursor moved during hold
                      (set! (.-current lerp-x) (.-current target-x))
                      (set! (.-current lerp-y) (.-current target-y))
                      (lock-scroll!)
                      ;; Start lerp loop (smoothly follows cursor)
                      (set! (.-current lerp-running) true)
                      (run-lerp-loop! {:lerp-running   lerp-running
                                       :lerp-raf       lerp-raf
                                       :lerp-x         lerp-x
                                       :lerp-y         lerp-y
                                       :target-x       target-x
                                       :target-y       target-y
                                       :set-drag-state set-drag-state})
                      ;; Transition from holding → dragging (shows ghost card)
                      (set-drag-state #(assoc % :holding? false :dragging? true)))
                    150))))]

    (hooks/use-effect [pointer-active?]
      (when pointer-active?
        (let [card    (:card drag-state)
              source  (:source drag-state)
              start-x (:start-x drag-state)
              start-y (:start-y drag-state)

              stop-lerp!
              (fn []
                (set! (.-current lerp-running) false)
                (when (.-current lerp-raf)
                  (js/cancelAnimationFrame (.-current lerp-raf))
                  (set! (.-current lerp-raf) nil)))

              reset!
              (fn []
                (stop-lerp!)
                (unlock-scroll!)
                (set-drag-state {:dragging?  false
                                 :holding?   false
                                 :card       nil
                                 :source     nil
                                 :start-x    0
                                 :start-y    0
                                 :x          0
                                 :y          0
                                 :hover-zone nil}))

              move-options #js {:passive false}

              move-fn
              (fn [e]
                (when (.-cancelable e)
                  (.preventDefault e))
                (let [cx (.-clientX e)
                      cy (.-clientY e)]
                  ;; Update lerp target (raw cursor; ghost follows via RAF loop)
                  (set! (.-current target-x) cx)
                  (set! (.-current target-y) cy)
                  ;; Auto-scroll near viewport edges (ported from Swapy createAutoScroller)
                  (let [vh    js/window.innerHeight
                        max-d 100
                        max-s 5
                        dt    (- 0 cy)
                        db    (- vh cy)]
                    (cond
                      (>= dt (- max-d)) (js/window.scrollBy 0 (- (remap (- max-d) 0 0 max-s dt)))
                      (<= db max-d)     (js/window.scrollBy 0 (remap max-d 0 0 max-s db))))
                  ;; Real-time zone highlight (updates on every pointermove)
                  (set-drag-state #(assoc % :hover-zone
                                          (detect-over-zone cx cy table-ref discard-ref hand-ref)))))

              mark-suppress-click!
              (fn []
                (set! (.-current suppress-next-click?) true)
                (js/setTimeout #(set! (.-current suppress-next-click?) false) 0))

              up-fn
              (fn [e]
                ;; Cancel hold timeout if drag hasn't started yet
                (when (.-current hold-timeout)
                  (js/clearTimeout (.-current hold-timeout))
                  (set! (.-current hold-timeout) nil))
                (let [x    (.-clientX e)
                      y    (.-clientY e)
                      dx   (- x start-x)
                      dy   (- y start-y)
                      dist (Math/sqrt (+ (* dx dx) (* dy dy)))
                      zone (detect-over-zone x y table-ref discard-ref hand-ref)]
                  (mark-suppress-click!)
                  (if (< dist 8)
                    ;; Click (pointer didn't move) — open modal
                    (if (= source :hand)
                      (set-modal-state {:show? true
                                        :confirm-click #(card.option/confirm-action game-state card set-game-state)
                                        :content card.option/card-options-component})
                      (set-modal-state {:show? true
                                        :confirm-click #(undo-play-action game-state card set-game-state)
                                        :content #(d/p (app.i18n/app-tr [:modal/undo?]))}))
                    ;; Drag — execute zone action
                    (cond
                      (and (= zone :table) (= source :hand))
                      ;; FLIP: capture before play-action, animate after re-render
                      (let [init-rects (capture-rects! table-ref)]
                        (play-action game-state card set-game-state)
                        (flip-animate! table-ref init-rects))

                      (and (= zone :discard) (= source :hand))
                      (discard-action game-state card set-game-state)

                      (and (= zone :hand) (= source :table))
                      (undo-play-action game-state card set-game-state)))
                  (reset!)))

              cancel-fn
              (fn []
                (when (.-current hold-timeout)
                  (js/clearTimeout (.-current hold-timeout))
                  (set! (.-current hold-timeout) nil))
                (reset!))]

          (.addEventListener js/document "pointermove"   move-fn move-options)
          (.addEventListener js/document "pointerup"     up-fn)
          (.addEventListener js/document "pointercancel" cancel-fn)
          (fn []
            (unlock-scroll!)
            (.removeEventListener js/document "pointermove"   move-fn move-options)
            (.removeEventListener js/document "pointerup"     up-fn)
            (.removeEventListener js/document "pointercancel" cancel-fn)))))

    (d/div {:style {:display         "flex"
                    :justify-content "space-between"
                    :align-items     "start"
                    :padding         "0 1.2rem"}}

           ;; Ghost card — follows cursor with lerp inertia (only visible during drag)
           (when (:dragging? drag-state)
             (d/div {:style {:position       "fixed"
                             :left           (str (- (:x drag-state) 50) "px")
                             :top            (str (- (:y drag-state) 70) "px")
                             :pointer-events "none"
                             :z-index        "100"
                             :transform      "rotate(4deg)"
                             :filter         "drop-shadow(0 8px 16px rgba(0,0,0,0.6))"}}
                    ($ card-component {:rank (:rank (:card drag-state))
                                       :suit (:suit (:card drag-state))})))

           (d/main {:id    "main-content"
                    :style {:display        "flex"
                            :flex-direction "column"}}
                   (hand-cards hand
                               {:card-click           (fn [card]
                                                        (when-not (.-current suppress-next-click?)
                                                          (set-modal-state
                                                           {:show? true
                                                            :confirm-click #(card.option/confirm-action game-state card set-game-state)
                                                            :content card.option/card-options-component})))
                                :on-card-pointer-down (fn [card e] (on-pointer-down card :hand e))
                                :hand-ref             hand-ref
                                :drop-active?         (and (:dragging? drag-state) (= (:source drag-state) :table))
                                :hover-zone           (:hover-zone drag-state)
                                :holding-card-id      (when (:holding? drag-state) (:id (:card drag-state)))
                                :dragging-card-id     (when (= (:source drag-state) :hand)
                                                        (:id (:card drag-state)))})
                   (table-cards table
                                {:card-click           (fn [card]
                                                         (when-not (.-current suppress-next-click?)
                                                           (set-modal-state
                                                            {:show? true
                                                             :confirm-click #(undo-play-action game-state card set-game-state)
                                                             :content #(d/p (app.i18n/app-tr [:modal/undo?]))})))
                                 :on-card-pointer-down (fn [card e] (on-pointer-down card :table e))
                                 :table-ref            table-ref
                                 :drop-active?         (and (:dragging? drag-state) (= (:source drag-state) :hand))
                                 :hover-zone           (:hover-zone drag-state)
                                 :holding-card-id      (when (:holding? drag-state) (:id (:card drag-state)))
                                 :dragging-card-id     (when (= (:source drag-state) :table)
                                                         (:id (:card drag-state)))}))

           (decks-section {:game-state     game-state
                           :set-game-state set-game-state
                           :discard-ref    discard-ref
                           :drop-active?   (and (:dragging? drag-state) (= (:source drag-state) :hand))
                           :hover-zone     (:hover-zone drag-state)}))))
