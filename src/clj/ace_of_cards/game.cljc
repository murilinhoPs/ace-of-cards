(ns clj.ace-of-cards.game
  (:require [clj.ace-of-cards.card :as card]
            [clj.ace-of-cards.utils :as utils]
            [schema.core :as s]))

(s/defschema Game {:deck [card/Card] 
                   :hand [card/Card] 
                   :table [card/Card] 
                   :discard-pile [card/Card]})

(def game-collections "pilhas/baralhos do jogo, mão, deck e pilha de descarte"
  #{:deck
    :hand
    :discard-pile
    :table})
(s/defschema GameCollection (apply s/enum game-collections))

(defn get-hand [game] (:hand game))
(defn get-discard-pile [game] (:discard-pile game))
(defn get-deck [game] (:deck game))
(defn get-table [game] (:table game))

(s/defn ^:private insert-jokers [deck]
  (let [joker {:rank "Joker" :suit :joker}]
    (conj deck joker joker)))

(s/defn create-game :- Game
  [ace-of-spades? :- s/Bool]
  (let [rank-limit (+ 1 (utils/rank-limit ace-of-spades?))
        cards (for [suit  card/suits
                    :when (not= suit :joker)
                    rank  (range 1 rank-limit)]
                (card/create-card ace-of-spades? suit rank))
        cards-including-joker (insert-jokers cards)]
    {:hand []
     :deck cards-including-joker
     :table []
     :discard-pile []}))
