(def rdom (js/require "react-dom/client"))
;;? or
(def createRoot (.-createRoot (js/require "react-dom/client")))

(def React (.-default (js/require "react")))

(defn App [] [:div {} "Hello World!"])

(def root (.createRoot js/ReactDOM (.getElementById js/document "root")))

(.render root [App {}])
