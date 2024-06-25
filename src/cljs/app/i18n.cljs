(ns app.i18n
  (:require [taoensso.tempura :as tempura :refer [tr]]))

(def i18n-dictionary
  {:en   {:example {:foo         ":en :example/foo text"
                    :foo_comment "Hello translator, please do x"
                    :bar {:baz ":en :example.bar/baz text"}
                    :greeting "Hello %s, how are you?"
                    :inline-markdown "<tag>**strong**</tag>"
                    :block-markdown* "<tag>**strong**</tag>"
                    :with-exclaim!   "<tag>**strong**</tag>"
                    :with-arguments  "Num %d = %s"
                    :greeting-alias :example/greeting
                    :baz-alias      :example.bar/baz}
          :header {:shuffle-deck "Shuffle Deck"
                   :new-game "New Game"
                   :reset-game "Reset Game"}
          :missing  "Missing translation"}
   :pt {:header {:shuffle-deck "Embaralhar"
                 :new-game "Novo Jogo"
                 :reset-game "Reiniciar"}
        :missing  "Texto não existe"}
   :it {:header {:shuffle-deck "Mescolare"
                 :new-game "Nuova partita"
                 :reset-game "Ricominciare"}}})

(def check-lang
  (let [js-language (.-language js/navigator)]
    (if (nil? js-language) :en js-language)))

(def opts {:dict i18n-dictionary})

(def app-tr (partial tr opts [check-lang]))
