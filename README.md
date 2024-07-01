# Ace of Cards - Fabula Ultima

It's a *web-app* that simulates the deck from the **Ace of Cards** class in the Fabula Ultima TTRPG. It creates the deck that this class uses in the RPG, so you don't have to use a real deck of cards.

I decided to create this app because I'm going to play this class in an online **RPG**, and since it's online, the **DM** and I thought it would be better to use an online tool for this. That way, he can see what I'm doing, and it makes it easier to play fully online. I couldn't find any tool for this, so I decided to create my own.

## Preview

[![](https://img.youtube.com/vi/oKZSHhYwdLE/0.jpg)](https://youtu.be/oKZSHhYwdLE)

## Features

Features added until now:

- Start and Restart the game
- See your current hand and the table (where you play the card)
  - **Both have a limit of 5**
- Draw, Play and Discard a card
- Shuffle Deck when needed
  - It re-shuffles the discard pile when deck is with 0 cards
- Return to your hand a card you played if you've selected the wrong card
- See the cards in the discard-pile and the deck
  - *Only see the card in the deck if you need (**it's not cool**)*
- Footer with player actions (ace of cards skills)
  - You can resolve the cards (Magic cards skill)
  - You can use Trap card skill
  - Mulligan and High or Low can be done manually

---

## Usage

First clone from [murilinhoPs/ace-of-cards](https://github.com/murilinhops/ace-of-cards)

What you need installed in your system ⚠ :

- [NodeJs](https://nodejs.org/en/)
- [NPM](https://www.npmjs.com/) that comes with **NodeJs**
- [Java SDK](https://www.oracle.com/java/technologies/downloads/) (Version 11+, Latest **LTS** Version recommended) I'm using **Java** Version **17**
- [Clojure Tools](https://clojure.org/guides/getting_started) current version **1.11v**

### Run project

- Before run any command you have to update *index.html* on **line 16** to:
  
        <script src="resources/js/main.js"></script>

- Run `npm install` to insall all front dependencies
- You can run `npm start` to start via **shadow-cljs** or connect via REPL (in VsCode for example *`ctrl/cmd + shift + p -> Calva: Start or Connect to a clojure REPL`*)
- Navigate to *[localhost:8020](http://localhost:8020)*

### Test

- Run the project's tests (they'll fail until you edit them):
  
        $ clojure -T:build test

### Bugs

I didn't find any bugs, but if you find one feel free to open an issue explaining the bug then I'll try to fix it!

---

## License

Copyright © 2024 murilinhoPs

Distributed under the Eclipse Public License version 1.0.
