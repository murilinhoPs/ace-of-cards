(ns app.components.modal.modal-base
  (:require [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]))

(defnc modal-base [{:keys [set-show-modal confirm-click content]}]
  (let [close-modal #(set-show-modal assoc :show? false)
        [continue? set-continue?] (hooks/use-state true)
        confirm-action (fn [] (confirm-click) (close-modal))]
    (<>
     (d/div {:class "dark-BG" :on-click close-modal})
     (d/div {:class "centered"}
            (d/div {:class "modal"}
                   (d/div {:class "modal-header"}
                          (d/h3 {:class "heading"} "Action Required"))
                   (d/button {:class "close-btn"  :on-click close-modal}
                             (d/i {:class "icon-x"}))
                   (d/div {:class "modal-content"}
                          ($ content {:set-continue? set-continue?}))
                   (d/div {:class "modal-actions"}
                          (d/div {:class "actions-container"}
                                 (d/button {:class "cancel-btn"
                                            :on-click close-modal}
                                           "Cancel")
                                 (d/button {:class "confirm-btn"
                                            :on-click confirm-action
                                            :disabled (not continue?)}
                                           "Confirm"))))))))
