package solitaire.strategy;

import solitaire.game.GameView;
import solitaire.game.Move;

/** Automatic play turned off: never suggests a move (RF28). */
public final class DisabledStrategy implements Strategy {

    @Override
    public Move suggest(GameView game) {
        return Move.none();
    }
}
