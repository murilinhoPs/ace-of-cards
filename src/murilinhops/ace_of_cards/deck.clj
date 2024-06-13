(ns murilinhops.ace-of-cards.deck
  (:require [murilinhops.ace-of-cards.card :as card]
            [murilinhops.ace-of-cards.utils :as utils]
            [schema.core :as s]))

(s/defschema Deck {:deck [card/Card] :discard-pile [card/Card]})

(s/defn insert-jokers [deck]
  (let [joker {:rank "Joker" :suit :joker}]
    (conj deck joker joker)))

(s/defn create-deck :- Deck
  [ace-of-spades? :- s/Bool]
  (let [rank-limit (+ 1 (utils/rank-limit ace-of-spades?))
        cards (for [suit  card/suits ;tipo generateList(lenght(13), data: {suit, rank})
                    :when (not= suit :joker)
                    rank  (range 1 rank-limit)]
                (card/create-card ace-of-spades? suit rank))
        cards-including-joker (insert-jokers cards)]
    {:deck cards-including-joker :discard-pile []}))

(defn shuffle-deck [deck]
  (update deck :deck shuffle))
