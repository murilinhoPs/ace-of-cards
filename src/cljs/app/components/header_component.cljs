(ns app.components.header-component
  (:require [app.i18n]
            [helix.core :refer [$ defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]))

(defnc shuffle-button [{:keys [on-click]}]
  (let [[shuffled? set-shuffled?] (hooks/use-state false)
        shuffle-deck' (app.i18n/app-tr [:header/shuffle-deck])
        shuffle-fn (fn [] (on-click) 
                     (set-shuffled? true) 
                     (js/setTimeout #(set-shuffled? false) 3000))]
    (d/button
     {:id "header-button"
      :on-click (when (not shuffled?) shuffle-fn)
      :disabled (when shuffled? true)
      :style {:background-color "var(--secondary-color)"
              :color "var(--text-color)"
              :border "none"
              :border-radius "12px"
              :padding-top (when shuffled? "3px")
              :font-weight "bold"
              :font-size ".9rem"
              :min-width "120px"}}
     (if shuffled?
       (d/i {:class "icon-circle-check-big"
             :style {:font-size "1.2rem"
                     :color "var(--green-check)"}})
       shuffle-deck'))))

(defnc header-component [{:keys [started? start-game restart-game shuffle-deck]}]
  (let [new-game'     (app.i18n/app-tr [:header/new-game])
        reset-game'   (app.i18n/app-tr [:header/reset-game])]
    (d/header {:class "header"
               :style {:display "flex"
                       :justify-content "space-between"
                       :margin "12px 12px 48px"
                       :padding ".4rem 12px 0"}}
              (d/h1 "Ace of Cards - Fabula Ultima")
              (d/div {:style {:display "flex"
                              :flex-direction "row"
                              :flex-wrap "wrap"
                              :gap "16px"
                              :align-self "center"}}
                     (when started? ($ shuffle-button {:on-click shuffle-deck}))
                     (d/button
                      {:id "header-button"
                       :on-click (if started? #(restart-game) #(start-game))
                       :style {:background-color  (if started? "var(--main-color)" "var(--secondary-color)")
                               :color "var(--text-color)"
                               :border "none"
                               :border-radius "12px"
                               :padding "8px"
                               :font-weight "bold"
                               :font-size ".9rem"
                               :max-height "36px"
                               :min-width "120px"}}
                      (if started? reset-game' new-game'))))))
