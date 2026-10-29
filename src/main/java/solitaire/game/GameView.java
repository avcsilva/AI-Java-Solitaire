package solitaire.game;

import java.util.List;
import java.util.Optional;
import solitaire.model.Card;

/** Read-only queries on the game state; none of them changes it (RF27). */
public interface GameView {

    Location stock();

    Location discard();

    /** The four foundations, in display order. */
    List<Location> foundations();

    /** The seven tableau columns, from left to right. */
    List<Location> columns();

    /** Location of a card that is accessible for moving, if any (RF10). */
    Optional<Location> locationOf(Card card);

    /**
     * Cards that would move together with {@code card}: in a column, the card up to the top of
     * the column (RF20); elsewhere, the card alone when it is the top. Empty when not movable.
     */
    List<Card> sequenceFrom(Card card);

    boolean isFaceUp(Card card);

    /** Whether the card is the face-up card closest to the base of its column (RF20). */
    boolean isLowestFaceUp(Card card);

    /** Whether the card is a face-up King already at the base of its column (RF20). */
    boolean isKingAtBase(Card card);

    /** Whether moving {@code card} to {@code target} is legal (RF21). */
    boolean canMove(Card card, Location target);

    /** Number of cards in the foundations (RF03). */
    int score();

    /** Whether all 52 cards are in the foundations (RF02). */
    boolean isWon();

    boolean canUndo();
}
