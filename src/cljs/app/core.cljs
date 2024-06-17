(ns cljs.app.core
  (:require ["react-dom/client" :as rdom]
            [helix.core :refer [$ defnc <>]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [clj.ace-of-cards.core :refer [new-game]]))

(defn _start-game [set-state]
  (let [data (new-game true)]
    (set-state assoc :hand (:hand data))
    (set-state assoc :deck (:deck data))
    (set-state assoc :discard-pile (:discard-pile data))))

(defn hand-cards [hand]
  (when (< 0 (count hand))
    (d/div {:style {:display "flex" 
                    :flex-direction "column"}}
           (for [card hand]
             (d/div {:style {:padding "8px 0px"
                             :display "flex"
                             :column-gap "16px"}}
                    (if (= :joker (:suit card))
                      (d/text (:rank card))
                      (<> (d/text (str "Valor: " (:rank card)))
                          (d/text (str "Naipe: " (:suit card))))))))))

(defnc app []
  (let [[state set-state] (hooks/use-state {:hand [], :deck [], :discard-pile []})
        start-game (fn [] (_start-game set-state))]
    (d/div
     (d/header {:class "header"}
               (d/h1 "Ace of Cards - Fabula Ultima"))
     (hand-cards (:hand state))
     (d/button  {:class "start-button", :on-click #(start-game)}
                (if (-> state :deck empty?) "New Game" "Reset Game")))))

(defonce root (rdom/createRoot (js/document.getElementById "app")))

(defn ^:export init []
  (js/console.log "Hello ace of cards")
  (.render root ($ app)))
