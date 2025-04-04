(ns app.components.decks.decks-section
  (:require [app.components.card-list-component :refer [card-list-component]]
            [app.components.decks.deck-component :refer [deck-component]]
            [app.components.decks.discard-pile-component :refer [discard-pile-component]]
            [helix.core :refer [$ defnc]]
            [helix.dom :as d]))

(defnc decks-section [{:keys [game-state set-game-state]}]
  (let [deck-count (-> game-state :deck count)
        discard-pile-count (-> game-state :discard-pile count)]
    (d/aside {:class "aside-decks"
              :style {:display "flex" 
                      :margin-left "1.2rem"
                      :align-items "end"
                      :align-self "center"
                      :flex-direction "column"
                      :gap "4rem"}}
             ($ deck-component {:count deck-count :game-state game-state :set-game-state set-game-state})
             (d/div {:class "discard-section"}
                    ($ card-list-component {:coll (-> game-state :discard-pile reverse)})
                    ($ discard-pile-component  {:count discard-pile-count})))))
