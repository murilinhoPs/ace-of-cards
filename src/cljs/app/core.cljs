(ns cljs.app.core
  (:require ["react-dom/client" :as rdom]
            [helix.core :refer [$ defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [clj.ace-of-cards.core :refer [new-game]]))

; show the hand (a list of cards)

(defnc app []
  (let [[state set-state] (hooks/use-state {:hand []})
        start-game (fn [] (let [data (new-game true)]
                            (println data)
                            (println (:hand data))
                            (set-state assoc :hand (:hand data))))]
    (d/div
     (d/h1 "Ace of Cards - Fabula Ultima")
     (d/input {:value (:hand state)
               :on-change #(set-state assoc :hand (.. % -target -value))})
     (d/button  {:on-click #(start-game)} "New Game"))))

(defonce root (rdom/createRoot (js/document.getElementById "app")))

(defn ^:export init []
  (js/console.log "Hello ace of cards")
  (.render root ($ app)))
