(ns murilinhops.ace-of-cards.deck
  (:require [murilinhops.ace-of-cards.card :as card]
            [murilinhops.ace-of-cards.utils :as utils]
            [schema.core :as s]))

(s/defschema Deck {:deck [card/Card] :discard-pile [card/Card]})

(s/defn create-deck :- Deck
  [ace-of-spades? :- s/Bool]
  (let [rank-limit (+ 1 (utils/rank-limit ace-of-spades?))
        all-cards (for [suit card/suits ;tipo generateList(lenght(13), data: {suit, rank})
                        rank (range 1 rank-limit)]
                    (card/create-card ace-of-spades? suit rank))]
    {:deck all-cards :discard-pile []}))

(create-deck false)
