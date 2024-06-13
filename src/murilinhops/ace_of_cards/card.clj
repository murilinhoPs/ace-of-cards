(ns murilinhops.ace-of-cards.card
  (:require [murilinhops.ace-of-cards.utils :as utils]
            [schema.core :as s]))

(def suits "naipe da carta"
  #{:hearts
    :clubs
    :diamonds
    :spades
    :joker})
(s/defschema Suit (apply s/enum suits))

(s/defschema Card {:suit Suit :rank (s/cond-pre s/Int s/Str)})
(s/defn create-card :- Card
  [ace-of-spades? :- s/Bool
   suit :- Suit
   rank :- s/Int]
  (let [valid-suit?  (contains? suits suit)
        valid-rank   (<= 1 rank (utils/rank-limit ace-of-spades?))
        current-rank (utils/convert-rank rank)]
    (if (and valid-suit? valid-rank)
      {:rank current-rank, :suit suit}
      (throw (IllegalArgumentException. "Invalid card parameters.")))))
