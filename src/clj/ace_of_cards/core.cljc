(ns clj.ace-of-cards.core 
  (:require [clj.ace-of-cards.actions :as actions]
            [clj.ace-of-cards.game :as game]
            [clj.ace-of-cards.skills :as skills]))

(defn new-game
  [ace-of-spades?]
  (->  (game/create-game ace-of-spades?)
       (actions/shuffle-deck)
       (actions/take-cards-from-deck 5)))

(def available-chars (reduce (fn [acc val]
                               (print (str "value:" val " char:" (char val) " "))
                               (print (str " " acc))
                               (str acc (char val))) "" (range 33 123)))

(defn greet
  "Callable entry point to the application."
  [data]
  (println (str "Hello, " (or (:name data) "World") "!")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (new-game (first args)))
