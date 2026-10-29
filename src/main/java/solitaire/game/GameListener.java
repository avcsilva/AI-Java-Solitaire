package solitaire.game;

/** Notified after every effective change of the game state, including undo (RF26). */
@FunctionalInterface
public interface GameListener {

    void onGameChanged();
}
