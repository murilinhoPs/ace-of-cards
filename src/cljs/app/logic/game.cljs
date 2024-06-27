(ns app.logic.game
  (:require [app.i18n]
            [clj.ace-of-cards.actions :as actions]
            [clj.ace-of-cards.card :refer [Card]]
            [clj.ace-of-cards.core :refer [new-game]]
            [clj.ace-of-cards.game :refer [Game]]
            [helix.dom :as d]
            [schema.core :as s]))

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
  (->> (actions/take-cards-from-deck state)
       (update-game-state set-state)))

(defn shuffle-deck
  [state set-state]
  (->> (actions/shuffle-deck state)
       (update-game-state set-state)))

(s/defn discard-action
  [game :- Game
   card :- Card
   set-game-state]
  (-> (actions/discard-cards game :hand card)
      set-game-state))

(s/defn play-action
  [game :- Game
   card :- Card
   set-game-state]
  (-> (actions/play-card game card)
      set-game-state))

(s/defn undo-play-action
  [game :- Game
   card :- Card
   set-game-state]
  (-> (actions/undo-play-card game card)
      set-game-state))
