(ns murilinhops.ace-of-cards.skills
  (:require [murilinhops.ace-of-cards.card :as card]
            [murilinhops.ace-of-cards.game :as game]
            [murilinhops.ace-of-cards.utils :as utils]
            [murilinhops.ace-of-cards.actions :as actions]
            [schema.core :as s]))

(defn magic-cards "Resolve cards" [])

(defn high-or-low [])

(s/defn mulligan "Descartar até [SL] cartas da sua mão e comprar esse número novamente do seu deck"
  [game :- game/Game
   skill-level :- s/Int
   cards :- [card/Card]]
  (let [valid-cards-count? (when (= (-> :hand game count) skill-level) true)]
    (if valid-cards-count?
      (-> (actions/discard-cards game :hand cards)
          (actions/take-cards-from-deck skill-level))
      (throw (IllegalArgumentException. "Cards count above SL")))))

(defn use-trap-card [])
