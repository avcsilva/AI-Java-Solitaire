package solitaire.game;

import solitaire.model.Card;

/**
 * A suggested play, coming from the player or from the automatic strategy (RF31).
 * The game checks its legality before executing it (RF21).
 */
public sealed interface Move permits Move.Draw, Move.Transfer, Move.None {

    /** Draw the top card of the stock (RF11). */
    record Draw() implements Move {
    }

    /** Move {@code card} (and, from a column, the cards on top of it) to {@code target}. */
    record Transfer(Card card, Location target) implements Move {
    }

    /** No play. */
    record None() implements Move {
    }

    static Move draw() {
        return new Draw();
    }

    static Move transfer(Card card, Location target) {
        return new Transfer(card, target);
    }

    static Move none() {
        return new None();
    }
}
