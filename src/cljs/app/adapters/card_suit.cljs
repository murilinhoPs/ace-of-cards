(ns app.adapters.card-suit 
  (:require [clj.ace-of-cards.card :refer [Suit]]
            [schema.core :as s]))

(s/defn card-suits->icon :- s/Str
  [suit :- Suit]
  (cond
    (= :hearts suit) "icon-heart"
    (= :spades suit) "icon-spade"
    (= :clubs suit) "icon-club"
    (= :diamonds suit) "icon-diamond"))