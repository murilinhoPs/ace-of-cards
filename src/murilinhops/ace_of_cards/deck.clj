(ns murilinhops.ace-of-cards.deck
  (:require [murilinhops.ace-of-cards.card :as card]
            [murilinhops.ace-of-cards.utils :as utils]
            [schema.core :as s]))
;A M B U CARALHO
(s/defschema Game {:deck [card/Card] :hand [card/Card] :discard-pile [card/Card]})

(s/defn insert-jokers [deck]
  (let [joker {:rank "Joker" :suit :joker}]
    (conj deck joker joker)))

(s/defn create-game :- Game
  [ace-of-spades? :- s/Bool]
  (let [rank-limit (+ 1 (utils/rank-limit ace-of-spades?))
        cards (for [suit  card/suits ;tipo generateList(lenght(13), data: {suit, rank})
                    :when (not= suit :joker)
                    rank  (range 1 rank-limit)]
                (card/create-card ace-of-spades? suit rank))
        cards-including-joker (insert-jokers cards)]
    {:hand []
     :deck cards-including-joker
     :discard-pile []}))

(defn shuffle-deck [game]
  (update game :deck shuffle))

(s/defn take-card-from-deck
  [game :- Game 
   coll :- s/Keyword]
  (if (empty? (:deck game))
    game ;TODO: add the discard-pile to the deck and shuffle it again
    (let [card (first (:deck game))
          updated-deck (update game :deck rest) ; !rest retorna todos os elementos da lista tirando o primeiro
          updated-game (update updated-deck coll conj card)]
     updated-game)))

(defn take-cards-x-times-from-deck [deck n]
  (reduce (fn [current-deck _] (take-card-from-deck current-deck :hand)) 
          deck 
          (range n)))

(s/defn select-card-from-deck [game]
  (filter (fn [item] (= item {:rank 5 :suit :clubs})) (:deck game)))
