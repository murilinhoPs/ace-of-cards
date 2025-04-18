(ns app.components.game-component
  (:require [app.components.cards.hand :refer [hand-cards]]
            [app.components.cards.table :refer [table-cards]]
            [app.logic.game :refer [undo-play-action]]
            [app.components.decks.decks-section :refer [decks-section]]
            [app.components.modal.card-options :as card.option]
            [app.i18n]
            [helix.core :refer [defnc]]
            [helix.dom :as d]))

(defnc game-component [{{:keys [hand table] :as game-state} :game-state
                        :keys [set-game-state set-modal-state]}]
  (d/div {:style {:display "flex"
                  :justify-content "space-between"
                  :align-items "start"
                  :padding "0 1.2rem"}}
         (d/main  {:id "main-content"
                   :style {:display "flex"
                           :flex-direction "column"}}
                  (hand-cards hand
                              {:card-click (fn [card] (set-modal-state
                                                       {:show? true
                                                        :confirm-click #(card.option/confirm-action game-state card set-game-state)
                                                        :content card.option/card-options-component}))})
                  (table-cards table
                               {:card-click   (fn [card] (set-modal-state
                                                          {:show? true
                                                           :confirm-click #(undo-play-action game-state card set-game-state)
                                                           :content #(d/p (app.i18n/app-tr [:modal/undo?]))}))}))
         (decks-section {:game-state game-state :set-game-state set-game-state})))
