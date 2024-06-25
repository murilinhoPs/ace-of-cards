(ns app.i18n
  (:require [taoensso.tempura :as tempura :refer [tr]]))

(def i18n-dictionary
  {:en   {:header {:shuffle-deck "Shuffle Deck"
                   :new-game "New Game"
                   :reset-game "Reset Game"}
          :cards {:hand "Hand"
                  :table "Table"}
          :decks {:draw "Draw"}
          :missing  "Missing translation"}

   :pt {:header {:shuffle-deck "Embaralhar"
                 :new-game "Novo Jogo"
                 :reset-game "Reiniciar"}
        :cards {:hand "Mão"
                :table "Mesa"}
        :decks {:draw "Comprar"}
        :missing  "Texto não existe"}

   :it {:header {:shuffle-deck "Mescolare"
                 :new-game "Nuova partita"
                 :reset-game "Ricominciare"}
        :cards {:hand "Mano"
                :table "Tavolo"}
        :decks {:draw "Comprare"}}})

(def check-lang
  (let [js-language (.-language js/navigator)]
    (if (nil? js-language) :en js-language)))

(def opts {:dict i18n-dictionary})

(def app-tr (partial tr opts [check-lang]))
