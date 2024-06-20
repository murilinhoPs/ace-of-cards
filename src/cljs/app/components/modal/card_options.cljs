(ns app.components.modal.card-options
  (:require [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [schema.core :as s]))

(defnc card-options-component [{:keys [set-continue?]}]
  (let [[checked? set-checked?]  (hooks/use-state {:first? false :second? false})]
    (hooks/use-effect 
     :once
     (set-continue? false))
    (d/span {:class "modal-options"}
           (d/article {:class "modal-option"
                       :style {:display "flex"}}
                      (d/input {:id "draw-option" 
                                :checked (:first? checked?)
                                :on-change  #((fn [] (print (.. % -target -value))
                                                (set-checked? (.. % -target -value))))
                                :type "radio"})
                      (d/label "Draw"))
           (d/article {:class "modal-option"
                       :style {:display "flex"}}
                      (d/input {:id "discard-option"
                                :checked (:second? checked?)
                                :on-change  #(set-checked? (.. % -target -value))
                                :type "radio"})
                      (d/label "Discard")))))

(defn card-action [set-state])
