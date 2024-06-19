(ns clj.ace-of-cards.skills
  (:require [clj.ace-of-cards.actions :as actions]
            [clj.ace-of-cards.card :as card]
            [clj.ace-of-cards.game :as game]
            [schema.core :as s]))

(defn resolve-magic-cards "Resolve cards" [])

(defn high-or-low [])

(s/defn mulligan "Descartar até [SL] cartas da sua mão e comprar esse número novamente do seu deck"
  [game :- game/Game
   skill-level :- s/Int
   cards :- [card/Card]]
  (let [valid-cards-count? (when (= (count cards) skill-level) true)]
    (if valid-cards-count?
      (-> (actions/discard-cards game :hand cards)
          (actions/take-cards-from-deck skill-level))
      (throw (ex-info "Cards count not match with SL" {:SL skill-level
                                                       :card-counts (count cards)})))))

(defn use-trap-card [])
