package solitaire.strategy;

import java.util.Optional;
import solitaire.game.GameView;
import solitaire.game.Location;
import solitaire.game.Move;
import solitaire.model.Card;

/**
 * Picks the first possible move in the priority order of RF29. Tableau moves are only made
 * when they reveal a card or empty a column, so automatic play never cycles (RF30).
 */
public final class PriorityStrategy implements Strategy {

    @Override
    public Move suggest(GameView game) {
        Location stock = game.stock();
        Location discard = game.discard();

        // 1. Draw when the discard is empty.
        if (discard.isEmpty() && !stock.isEmpty()) {
            return Move.draw();
        }

        Card discardTop = discard.peek();
        if (discardTop != null) {
            // 2. Discard -> foundation.
            Optional<Location> foundation = firstAccepting(game, discardTop, game.foundations());
            if (foundation.isPresent()) {
                return Move.transfer(discardTop, foundation.get());
            }
            // 3. Discard -> tableau.
            Optional<Location> column = firstAccepting(game, discardTop, game.columns());
            if (column.isPresent()) {
                return Move.transfer(discardTop, column.get());
            }
        }

        // 4. Column top -> foundation.
        for (Location column : game.columns()) {
            Card top = column.peek();
            if (top != null) {
                Optional<Location> foundation = firstAccepting(game, top, game.foundations());
                if (foundation.isPresent()) {
                    return Move.transfer(top, foundation.get());
                }
            }
        }

        // 5. Tableau move that reveals a hidden card or empties a column.
        for (Location column : game.columns()) {
            for (Card card : column.cards()) {
                if (game.isLowestFaceUp(card) && !game.isKingAtBase(card)) {
                    Optional<Location> target = firstAccepting(game, card, game.columns());
                    if (target.isPresent()) {
                        return Move.transfer(card, target.get());
                    }
                }
            }
        }

        // 6. Draw.
        if (!stock.isEmpty()) {
            return Move.draw();
        }

        // 7. No move.
        return Move.none();
    }

    private static Optional<Location> firstAccepting(GameView game, Card card, Iterable<Location> targets) {
        for (Location target : targets) {
            if (game.canMove(card, target)) {
                return Optional.of(target);
            }
        }
        return Optional.empty();
    }
}
