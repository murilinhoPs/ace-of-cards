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
  (let [[drag-state set-drag-state] (hooks/use-state {:dragging? false
                                                      :card      nil
                                                      :source    nil
                                                      :start-x   0
                                                      :start-y   0
                                                      :x         0
                                                      :y         0})
        table-ref   (hooks/use-ref nil)
        discard-ref (hooks/use-ref nil)
        hand-ref    (hooks/use-ref nil)
        on-pointer-down (fn [card source e]
                          (.preventDefault e)
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
              reset!  #(set-drag-state {:dragging? false
                                        :card      nil
                                        :source    nil
                                        :start-x   0
                                        :start-y   0
                                        :x         0
                                        :y         0})
              move-fn (fn [e]
                        (set-drag-state #(assoc %
                                                :x (.-clientX e)
                                                :y (.-clientY e))))
              up-fn   (fn [e]
                        (let [x    (.-clientX e)
                              y    (.-clientY e)
                              dx   (- x start-x)
                              dy   (- y start-y)
                              dist (js/Math.sqrt (+ (* dx dx) (* dy dy)))
                              zone (detect-over-zone x y table-ref discard-ref hand-ref)]
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
                          (reset!)))]
          (.addEventListener js/document "pointermove" move-fn)
          (.addEventListener js/document "pointerup"   up-fn)
          (fn []
            (.removeEventListener js/document "pointermove" move-fn)
            (.removeEventListener js/document "pointerup"   up-fn)))))

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
                               {:card-click           (fn [card] (set-modal-state
                                                                   {:show? true
                                                                    :confirm-click #(card.option/confirm-action game-state card set-game-state)
                                                                    :content card.option/card-options-component}))
                                :on-card-pointer-down (fn [card e] (on-pointer-down card :hand e))
                                :hand-ref             hand-ref
                                :dragging-card-id     (when (= (:source drag-state) :hand)
                                                        (:id (:card drag-state)))})
                   (table-cards table
                                {:card-click           (fn [card] (set-modal-state
                                                                    {:show? true
                                                                     :confirm-click #(undo-play-action game-state card set-game-state)
                                                                     :content #(d/p (app.i18n/app-tr [:modal/undo?]))}))
                                 :on-card-pointer-down (fn [card e] (on-pointer-down card :table e))
                                 :table-ref            table-ref
                                 :drop-active?         (and (:dragging? drag-state) (= (:source drag-state) :hand))
                                 :dragging-card-id     (when (= (:source drag-state) :table)
                                                         (:id (:card drag-state)))}))

           (decks-section {:game-state     game-state
                           :set-game-state set-game-state
                           :discard-ref    discard-ref
                           :drop-active?   (and (:dragging? drag-state) (= (:source drag-state) :hand))}))))
