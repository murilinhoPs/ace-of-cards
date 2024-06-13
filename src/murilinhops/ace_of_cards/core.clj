(ns murilinhops.ace-of-cards.core
  (:gen-class))

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
  (greet {:name (first args)}))
