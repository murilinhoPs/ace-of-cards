(ns app.components.game-component
  (:require [app.components.cards.hand :refer [hand-cards]]
            [app.components.cards.table :refer [table-cards]]
            [app.components.decks.decks-section :refer [decks-section]]
            [app.components.modal.card-options :as card.option]
            [helix.core :refer [$ defnc]]
            [helix.dom :as d]))

(defnc game-component [{:keys [game-state set-game-state set-modal-state]}]
  (d/div {:style {:display "flex" :justify-content 
                  "space-between"
                  :align-items "center"}}
         (d/main  {:style {:align-self "start"}}
                  (hand-cards (:hand game-state)
                              {:card-click (fn [card] (set-modal-state
                                                       {:show? true
                                                        :confirm-click #(card.option/confirm-action game-state card set-game-state)
                                                        :content card.option/card-options-component}))})
                  (table-cards (:table game-state)))
         ($ decks-section {:game-state game-state :set-game-state set-game-state})))
