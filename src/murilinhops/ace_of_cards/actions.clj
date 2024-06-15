(ns murilinhops.ace-of-cards.actions
  (:require [murilinhops.ace-of-cards.card :as card]
            [murilinhops.ace-of-cards.game :as game]
            [murilinhops.ace-of-cards.utils :as utils :refer [return-sequential]]
            [schema.core :as s]))

(defn shuffle-deck [game]
  (update game :deck shuffle))

(s/defn ^:private take-card-from-deck
  "Compro carta do deck e escolho onde quero colocar, na mão ou na pilha de descarte"
  [game :- game/Game
   coll :- game/GameCollection]
  (if (empty? (:deck game))
    [game nil] ;TODO: add the discard-pile to the deck and shuffle it again
    (let [card (first (:deck game))
          updated-deck (update game :deck rest)
          updated-game (update updated-deck coll conj card)]
      [updated-game card])))

(defn take-cards-from-deck [game & [n]]
  (-> (reduce (fn [[current-deck _] _] (take-card-from-deck current-deck :hand))
              [game nil] (range (or n 1)))
      first))

(s/defn ^:private discard-card :- game/Game
  [game :- game/Game
   coll :- game/GameCollection
   card :- card/Card]
  (let [updated-hand (into [] (remove #(= % card) (-> game coll)))
        updated-game (update game :discard-pile conj card)]
    (assoc updated-game :hand updated-hand)))

(s/defn discard-cards :- game/Game
  [game :- game/Game
   coll :- game/GameCollection
   cards :- [card/Card]]
  (let [actual-cards (return-sequential cards)]
    (reduce (fn [current-game card] (discard-card current-game coll card))
            game actual-cards)))

(s/defn ^:private select-card
  [game :- game/Game
   coll :- game/GameCollection
   card :- card/Card] ;{:rank 5 :suit :clubs} 
  (-> (filter (fn [item] (= item card)) (coll game))
      first))

(s/defn select-cards :- [card/Card]
  [deck :- game/Game
   coll :- game/GameCollection
   cards :- [card/Card]]
  (let [actual-cards (if (sequential? cards) cards [cards])]
    (reduce (fn [card-list card]
              (->> (select-card deck coll card)
                   (utils/condj card-list)))
            [] actual-cards)))
