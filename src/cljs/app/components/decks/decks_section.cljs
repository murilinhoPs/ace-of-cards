(ns app.components.decks.decks-section
  (:require [app.components.card-list-component :refer [card-list-component]]
            [app.components.decks.deck-component :refer [deck-component]]
            [app.components.decks.discard-pile-component :refer [discard-pile-component]]
            [helix.core :refer [$ defnc]]
            [helix.dom :as d]))

(defnc decks-section [{:keys [game-state set-game-state]}]
  (let [deck-count (-> game-state :deck count)
        discard-pile-count (-> game-state :discard-pile count)]
    (d/aside {:style {:display "flex" 
                      :margin-top "6.4rem"
                      :justify-content "end"
                      :align-items "end"
                      :flex-direction "column"
                      :gap "8rem"}}
             (d/div {:style {:display "flex"
                             :max-height "170px"
                             :gap "2rem"}}
                    ($ card-list-component {:coll (:deck game-state)})
                    ($ deck-component {:count deck-count :game-state game-state :set-game-state set-game-state}))
             (d/div {:style {:display "flex"
                             :gap "2rem"
                             :justify-content "end"}}
                    ($ card-list-component {:coll (-> game-state :discard-pile reverse)})
                    ($ discard-pile-component  {:count discard-pile-count})))))
