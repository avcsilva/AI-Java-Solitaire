package solitaire.game;

import java.util.List;
import solitaire.model.Card;
import solitaire.model.Rank;

/** A foundation, built up by suit from Ace to King, one card at a time (RF14, RF15). */
final class Foundation extends Pile {

    @Override
    public boolean accepts(List<Card> sequence) {
        if (sequence.size() != 1) {
            return false;
        }
        Card card = sequence.get(0);
        Card top = peek();
        if (top == null) {
            return card.rank() == Rank.ACE;
        }
        return card.suit() == top.suit() && card.rank().isImmediatelyAbove(top.rank());
    }
}
