(ns app.components.decks.decks-section 
  (:require [app.components.decks.deck-component :refer [deck-component]]
            [app.components.decks.discard-pile-component :refer [discard-pile-component]]
            [helix.core :refer [$ defnc]]
            [helix.dom :as d]))

(defnc decks-section [{:keys [game-state set-game-state]}]
  (d/aside {:style {:display "flex"
                    :align-items "center"
                    :justify-content "center"
                    :flex-direction "column"
                    :height "80vh"
                    :gap "120px"}}
           ($ deck-component {:count (-> game-state :deck count)
                              :game-state game-state
                              :set-game-state set-game-state})
           ($ discard-pile-component  {:count (-> game-state :discard-pile count)})))
