(ns clj.ace-of-cards.actions
  (:require [clj.ace-of-cards.card :as card]
            [clj.ace-of-cards.game :as game]
            [clj.ace-of-cards.utils :as utils :refer [return-sequential]]
            [schema.core :as s]))

(s/defn ^:private discard-card :- game/Game
  [game :- game/Game
   card :- card/Card
   current-coll :- game/GameCollection
   target-coll :- game/GameCollection]
  (let [updated-coll (into [] (remove #(= % card) (-> game current-coll)))
        updated-game (update game target-coll conj card)]
    (assoc updated-game current-coll updated-coll)))

(s/defn ^:private add-discard-pile-into-deck
  [game :- game/Game]
  (reduce (fn [current-game card] (discard-card current-game card :discard-pile :deck))
          game (:discard-pile game)))

(s/defn shuffle-deck :- game/Game
  [game :- game/Game]
  (if (-> game :deck empty?)
    (-> (add-discard-pile-into-deck game)
        (shuffle-deck))
    (update game :deck shuffle)))

(s/defn ^:private take-card-from-deck
  "Compro carta do deck e escolho onde quero colocar, na mão ou na pilha de descarte"
  [game :- game/Game
   coll :- game/GameCollection]
  (if (-> game :deck empty?)
    (-> (shuffle-deck game) (take-card-from-deck coll)) 
    (if (<= utils/max-cards (-> game :hand count))
      [game nil]
      (let [card (first (:deck game))
            updated-deck (update game :deck rest)
            updated-game (update updated-deck coll conj card)]
        [updated-game card]))))

(defn take-cards-from-deck [game & [n]]
  (-> (reduce (fn [[current-deck _] _] (take-card-from-deck current-deck :hand))
              [game nil] (range (or n 1)))
      first))

(s/defn discard-cards :- game/Game
  "Usually the collection will be the :hand"
  [game :- game/Game
   coll :- game/GameCollection
   cards :- [card/Card]]
  (let [actual-cards (return-sequential cards)]
    (reduce (fn [current-game card] (discard-card current-game card coll :discard-pile))
            game actual-cards)))

(s/defn ^:private select-card
  [game :- game/Game
   coll :- game/GameCollection
   card :- card/Card] ;{:rank 5 :suit :clubs} 
  (-> (filter (fn [item] (= item card)) (coll game))
      first))

(s/defn select-cards :- [card/Card]
  [game :- game/Game
   coll :- game/GameCollection
   cards :- [card/Card]]
  (let [actual-cards (return-sequential cards)]
    (reduce (fn [card-list card]
              (->> (select-card game coll card)
                   (utils/condj card-list)))
            [] actual-cards)))

(s/defn play-card :- game/Game
  [game :- game/Game
   card :- card/Card]
  (if (> utils/max-cards (-> game :table count))
    (discard-card game card :hand :table)
    game))

(s/defn undo-play-card :- game/Game
  [game :- game/Game
   card :- card/Card]
  (discard-card game card :table :hand))
