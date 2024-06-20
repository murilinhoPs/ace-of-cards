(ns clj.ace-of-cards.card
  (:require [clj.ace-of-cards.utils :as utils]
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
        valid-rank?  (<= 1 rank (utils/rank-limit ace-of-spades?))
        current-rank (utils/convert-rank rank)]
    (if (and valid-suit? valid-rank?)
      {:rank current-rank, :suit suit, :id (random-uuid)}
      (throw (ex-info "Invalid card parameters." {:valid-suit valid-suit?
                                                  :valid-rank valid-rank?})))))
