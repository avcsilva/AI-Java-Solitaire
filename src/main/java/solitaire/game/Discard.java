package solitaire.game;

import java.util.List;
import solitaire.model.Card;

/** The face-up discard; it only receives cards by drawing, never by a direct move (RF13). */
final class Discard extends Pile {

    @Override
    public boolean accepts(List<Card> sequence) {
        return false;
    }
}
