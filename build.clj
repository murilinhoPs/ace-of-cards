(ns build
  (:refer-clojure :exclude [test])
  (:require [clojure.tools.deps :as t]
            [clojure.tools.build.api :as b]))

(def lib 'ace-of-cards)
(def main 'clj.ace-of-cards.core)
(def class-dir "target/classes")

(defn test "Run all the tests." [opts]
  (println "\nRunning tests...")
  (let [basis    (b/create-basis {:aliases [:test]})
        combined (t/combine-aliases basis [:test])
        cmds     (b/java-command
                  {:basis basis
                   :java-opts (:jvm-opts combined)
                   :main      'clojure.main
                   :main-args ["-m" "cognitect.test-runner"]})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit) (throw (ex-info "Tests failed" {}))))
  opts)

(defn- uber-opts [opts]
  (assoc opts
         :lib lib
         :main main
         :uber-file (format "target/%s-app.jar" lib)
         :basis (b/create-basis {:project "deps.edn"})
         :class-dir class-dir
         :src-dirs ["src"]))

(defn ci "Run the CI pipeline of tests (and build the uberjar)." [opts]
  (test opts)
  (b/delete {:path "target"})
  (let [opts (uber-opts opts)]
    (println "\nCopying source...")
    (b/copy-dir {:src-dirs ["resources" "src"] 
                 :target-dir class-dir})
    (println (str "\nCompiling " main "..."))
    (b/compile-clj opts)
    (println "\nBuilding JAR...") 
    (b/uber opts))
  opts)
