(ns app.logic.game
  (:require [app.i18n]
            [clj.ace-of-cards.actions :as actions]
            [clj.ace-of-cards.core :refer [new-game]]
            [helix.dom :as d]))

(defn update-game-state
  [set-state data]
  (set-state assoc :hand (:hand data))
  (set-state assoc :deck (:deck data))
  (set-state assoc :table (:table data))
  (set-state assoc :discard-pile (:discard-pile data)))

(defn start-game [set-started? set-game-state]
  (set-started? true)
  (update-game-state set-game-state (new-game true)))

(defn restart-game
  [set-state restart-fn]
  (set-state {:show? true
              :confirm-click restart-fn
              :content #(d/p (app.i18n/app-tr [:modal/restart?]))}))

(defn draw-card
  [state set-state]
  (when (< (-> state :hand count) 5)
    (->> (actions/take-cards-from-deck state)
         (update-game-state set-state))))

(defn shuffle-deck
  [state set-state]
  (->> (actions/shuffle-deck state)
       (update-game-state set-state)))
