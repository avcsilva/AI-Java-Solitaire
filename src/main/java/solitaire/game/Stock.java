package solitaire.game;

import java.util.List;
import solitaire.model.Card;

/** The face-down stock; cards leave it only by drawing (RF11). */
final class Stock extends Pile {

    @Override
    public boolean isFaceUp(Card card) {
        return false;
    }

    @Override
    List<Card> movableSequenceFrom(Card card) {
        return List.of();
    }

    @Override
    public boolean accepts(List<Card> sequence) {
        return false;
    }
}
