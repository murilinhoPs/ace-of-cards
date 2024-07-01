(ns app.components.header-component
  (:require [app.i18n]
            [helix.core :refer [defnc]]
            [helix.dom :as d]))

(defnc header-component [{:keys [started? start-game restart-game shuffle-deck]}]
  (let [shuffle-deck' (app.i18n/app-tr [:header/shuffle-deck])
        new-game'     (app.i18n/app-tr [:header/new-game])
        reset-game'   (app.i18n/app-tr [:header/reset-game])]
    (d/header {:class "header"
               :style {:display "flex"
                       :justify-content "space-between"
                       :margin "12px 12px 48px"
                       :padding-top ".4rem"}}
              (d/h1 "Ace of Cards - Fabula Ultima")
              (d/div {:style {:display "flex"
                              :flex-direction "row"
                              :flex-wrap "wrap"
                              :gap "16px"
                              :align-self "center"}}
                     (when started? (d/button
                                     {:id "header-button"
                                      :on-click #(shuffle-deck)
                                      :style {:background-color "var(--secondary-color)"
                                              :color "var(--text-color)"
                                              :border "none"
                                              :border-radius "12px"
                                              :padding "8px"
                                              :font-weight "500"
                                              :max-height "36px"
                                              :min-width "120px"}}
                                     shuffle-deck'))
                     (d/button
                      {:id "header-button"
                       :on-click (if started? #(restart-game) #(start-game))
                       :style {:background-color  (if started? "var(--main-color)" "var(--secondary-color)")
                               :color "var(--text-color)"
                               :border "none"
                               :border-radius "12px"
                               :padding "8px"
                               :font-weight "500"
                               :max-height "36px"
                               :min-width "120px"}}
                      (if started? reset-game' new-game'))))))
