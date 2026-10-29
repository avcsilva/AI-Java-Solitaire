package solitaire.game;

import java.util.List;
import solitaire.model.Card;

/**
 * A place that holds cards: a tableau column, a foundation, the stock or the discard (RF10).
 * This view is read-only (RF27); only the game engine changes locations.
 */
public interface Location {

    boolean isEmpty();

    /** Top card, or {@code null} when empty (RF09). */
    Card peek();

    /** Cards from base to top, as an immutable snapshot. */
    List<Card> cards();

    boolean contains(Card card);

    /** Whether the card, which must be in this location, is face up. */
    boolean isFaceUp(Card card);

    /** Whether this location's stacking rules accept the sequence on top of it (RF13, RF15, RF17). */
    boolean accepts(List<Card> sequence);
}
