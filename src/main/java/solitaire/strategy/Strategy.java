package solitaire.strategy;

import solitaire.game.GameView;
import solitaire.game.Move;

/**
 * Decides an automatic move using only read-only queries (RF31, RNF04).
 * It never changes the game; the engine executes the suggestion.
 */
@FunctionalInterface
public interface Strategy {

    Move suggest(GameView game);
}
