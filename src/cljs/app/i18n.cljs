(ns app.i18n
  (:require [taoensso.tempura :as tempura :refer [tr]]))

(def i18n-dictionary
  {:en   {:header {:shuffle-deck "Shuffle Deck"
                   :new-game "New Game"
                   :reset-game "Reset Game"}
          :cards {:hand "Hand"
                  :table "Table"}
          :decks {:draw "Draw"}
          :card-list {:cards "Cards"}
          :modal {:cancel "Cancel"
                  :ok "Confirm"
                  :heading "Action Required"
                  :restart? "Are you sure you want to restart game?"
                  :undo? "Move card back to your hand?"
                  :card-options {:play "Play"
                                 :discard "Discard"
                                 :select-action "Select what you want to do with this card:"}}
          :missing  "Missing translation"}

   :pt {:header {:shuffle-deck "Embaralhar"
                 :new-game "Novo Jogo"
                 :reset-game "Reiniciar"}
        :cards {:hand "Mão"
                :table "Mesa"}
        :decks {:draw "Comprar"}
        :card-list {:cards "Cartas"}
        :modal {:cancel "Cancelar"
                :ok "Confirmar"
                :heading "Atenção"
                :restart? "Tem certeza que quer reiniciar o jogo?"
                :undo? "Voltar essa carta para sua mão?"
                :card-options {:play "Jogar"
                               :discard "Descartar"
                               :select-action "Escolha o que deseja fazer com essa carta:"}}
        :missing  "Texto não existe"}

   :it {:header {:shuffle-deck "Mescolare"
                 :new-game "Nuova partita"
                 :reset-game "Ricominciare"}
        :cards {:hand "Mano"
                :table "Tavolo"}
        :decks {:draw "Comprare"}
        :card-list {:cards "Carte"}
        :modal {:cancel "Annullare"
                :ok "Confermare"
                :heading "Avviso"
                :restart? "Vuoi riavviare il gioco?"
                :undo? "Riportare la carta in mano?"
                :card-options {:play "Usare"
                               :discard "Scartare"
                               :select-action "Che cosa vuoi fare con questa carte:"}}}})

(def check-lang
  (let [js-language (.-language js/navigator)]
    (if (nil? js-language) :en js-language)))

(def opts {:dict i18n-dictionary})

(def app-tr (partial tr opts [check-lang]))
