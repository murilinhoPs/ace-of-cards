(ns app.adapters.card-suit 
  (:require [clj.ace-of-cards.card :refer [Suit]]
            [schema.core :as s]))

(s/defn card-suits->icon :- s/Str
  [suit :- Suit]
  (cond
    (= :hearts suit) "heart"
    (= :spades suit) "spade"
    (= :clubs suit) "club"
    (= :diamonds suit) "diamond"))