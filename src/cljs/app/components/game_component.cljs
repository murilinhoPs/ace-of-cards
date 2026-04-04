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

(defnc game-component [{{:keys [hand table] :as game-state} :game-state
                        :keys [set-game-state set-modal-state]}]
  (let [mobile-device? (or (< 0 (.-maxTouchPoints js/navigator))
                           (boolean (re-find #"Mobi|Android|iPhone|iPad|iPod"
                                             (or (.-userAgent js/navigator) ""))))
        scroll-lock-ref (hooks/use-ref {:locked? false
                                        :scroll-x 0
                                        :scroll-y 0
                                        :html-overflow ""
                                        :body-overflow ""
                                        :body-position ""
                                        :body-top ""
                                        :body-left ""
                                        :body-right ""
                                        :body-width ""
                                        :body-touch-action ""})
        lock-scroll! (fn []
                       (when mobile-device?
                         (let [state (.-current scroll-lock-ref)]
                           (when-not (:locked? state)
                             (let [html-style (.-style (.-documentElement js/document))
                                   body-style (.-style (.-body js/document))
                                   sx (or (.-scrollX js/window) (.-pageXOffset js/window) 0)
                                   sy (or (.-scrollY js/window) (.-pageYOffset js/window) 0)]
                               (set! (.-current scroll-lock-ref)
                                     {:locked? true
                                      :scroll-x sx
                                      :scroll-y sy
                                      :html-overflow (.-overflow html-style)
                                      :body-overflow (.-overflow body-style)
                                      :body-position (.-position body-style)
                                      :body-top (.-top body-style)
                                      :body-left (.-left body-style)
                                      :body-right (.-right body-style)
                                      :body-width (.-width body-style)
                                      :body-touch-action (.-touchAction body-style)})
                               (set! (.-overflow html-style) "hidden")
                               (set! (.-overflow body-style) "hidden")
                               (set! (.-position body-style) "fixed")
                               (set! (.-top body-style) (str (- sy) "px"))
                               (set! (.-left body-style) (str (- sx) "px"))
                               (set! (.-right body-style) "0")
                               (set! (.-width body-style) "100%")
                               (set! (.-touchAction body-style) "none"))))))
        unlock-scroll! (fn []
                         (let [{:keys [locked? scroll-x scroll-y html-overflow body-overflow body-position body-top body-left body-right body-width body-touch-action]}
                               (.-current scroll-lock-ref)]
                           (when locked?
                             (let [html-style (.-style (.-documentElement js/document))
                                   body-style (.-style (.-body js/document))]
                               (set! (.-overflow html-style) html-overflow)
                               (set! (.-overflow body-style) body-overflow)
                               (set! (.-position body-style) body-position)
                               (set! (.-top body-style) body-top)
                               (set! (.-left body-style) body-left)
                               (set! (.-right body-style) body-right)
                               (set! (.-width body-style) body-width)
                               (set! (.-touchAction body-style) body-touch-action)
                               (.scrollTo js/window scroll-x scroll-y)
                               (set! (.-current scroll-lock-ref) (assoc (.-current scroll-lock-ref) :locked? false))))))
        [drag-state set-drag-state] (hooks/use-state {:dragging? false
                                                      :card      nil
                                                      :source    nil
                                                      :start-x   0
                                                      :start-y   0
                                                      :x         0
                                                      :y         0})
        table-ref   (hooks/use-ref nil)
        discard-ref (hooks/use-ref nil)
        hand-ref    (hooks/use-ref nil)
        suppress-next-click? (hooks/use-ref false)
        on-pointer-down (fn [card source e]
                          (.preventDefault e)
                          (lock-scroll!)
                          (let [cx (.-clientX e)
                                cy (.-clientY e)]
                            (set-drag-state {:dragging? true
                                             :card      card
                                             :source    source
                                             :start-x   cx
                                             :start-y   cy
                                             :x         cx
                                             :y         cy})))]

    (hooks/use-effect [(:dragging? drag-state)]
      (when (:dragging? drag-state)
        (let [card    (:card drag-state)
              source  (:source drag-state)
              start-x (:start-x drag-state)
              start-y (:start-y drag-state)
              move-options #js {:passive false}
              reset!  #(do
                         (unlock-scroll!)
                         (set-drag-state {:dragging? false
                                          :card      nil
                                          :source    nil
                                          :start-x   0
                                          :start-y   0
                                          :x         0
                                          :y         0}))
              move-fn (fn [e]
                        (when (.-cancelable e)
                          (.preventDefault e))
                        (set-drag-state #(assoc %
                                                :x (.-clientX e)
                                                :y (.-clientY e))))
              mark-suppress-click! (fn []
                                     (set! (.-current suppress-next-click?) true)
                                     (js/setTimeout #(set! (.-current suppress-next-click?) false) 0))
              up-fn   (fn [e]
                        (let [x    (.-clientX e)
                              y    (.-clientY e)
                              dx   (- x start-x)
                              dy   (- y start-y)
                              dist (js/Math.sqrt (+ (* dx dx) (* dy dy)))
                              zone (detect-over-zone x y table-ref discard-ref hand-ref)]
                          (mark-suppress-click!)
                          (if (< dist 8)
                            (if (= source :hand)
                              (set-modal-state {:show? true
                                                :confirm-click #(card.option/confirm-action game-state card set-game-state)
                                                :content card.option/card-options-component})
                              (set-modal-state {:show? true
                                                :confirm-click #(undo-play-action game-state card set-game-state)
                                                :content #(d/p (app.i18n/app-tr [:modal/undo?]))}))
                            (cond
                              (and (= zone :table)   (= source :hand))  (play-action game-state card set-game-state)
                              (and (= zone :discard) (= source :hand))  (discard-action game-state card set-game-state)
                              (and (= zone :hand)    (= source :table)) (undo-play-action game-state card set-game-state)))
                          (reset!)))
              cancel-fn (fn []
                          (reset!))]
          (.addEventListener js/document "pointermove" move-fn move-options)
          (.addEventListener js/document "pointerup"   up-fn)
          (.addEventListener js/document "pointercancel" cancel-fn)
          (fn []
            (unlock-scroll!)
            (.removeEventListener js/document "pointermove" move-fn move-options)
            (.removeEventListener js/document "pointerup"   up-fn)
            (.removeEventListener js/document "pointercancel" cancel-fn))))) 
    (d/div {:style {:display "flex"
                    :justify-content "space-between"
                    :align-items "start"
                    :padding "0 1.2rem"}}

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

           (d/main {:id "main-content"
                    :style {:display "flex"
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
                                 :dragging-card-id     (when (= (:source drag-state) :table)
                                                         (:id (:card drag-state)))}))

           (decks-section {:game-state     game-state
                           :set-game-state set-game-state
                           :discard-ref    discard-ref
                           :drop-active?   (and (:dragging? drag-state) (= (:source drag-state) :hand))}))))
