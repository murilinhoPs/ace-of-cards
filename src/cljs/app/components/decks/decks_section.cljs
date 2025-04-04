(ns app.components.decks.decks-section
  (:require [app.components.card-list-component :refer [card-list-component]]
            [app.components.decks.deck-component :refer [deck-component]]
            [app.components.decks.discard-pile-component :refer [discard-pile-component]]
            [helix.core :refer [$]]
            [helix.dom :as d]))

(defn decks-section [& {:keys [game-state set-game-state]}]
  (let [deck-count (-> game-state :deck count)
        discard-pile-count (-> game-state :discard-pile count)
        hand-count (-> game-state :hand count)]
    (d/aside {:class "aside-decks"}
             ($ deck-component {:hand-count hand-count :count deck-count :game-state game-state :set-game-state set-game-state})
             (d/div {:class "discard-section"}
                    ($ card-list-component {:coll (-> game-state :discard-pile reverse)})
                    ($ discard-pile-component  {:count discard-pile-count})))))
