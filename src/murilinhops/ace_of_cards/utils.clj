(ns murilinhops.ace-of-cards.utils 
  (:require [schema.core :as s]))

(defn rank-limit [ace-of-spades?]
  (if ace-of-spades? 7 13))

(s/defn convert-rank
  [rank :- s/Int]
  (cond
    (= 1 rank)  "Às"
    (= 11 rank) "J"
    (= 12 rank) "Q"
    (= 13 rank) "K"
    :else rank))
