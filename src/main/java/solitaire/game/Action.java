package solitaire.game;

/**
 * A reversible change to the game state (RF22). Simple, composite and null actions are all
 * handled through this interface, without special cases (RNF02).
 */
public interface Action {

    void execute();

    /** Restores the state as it was before {@link #execute()}. */
    void undo();

    /** Whether executing this action changes the game state. */
    boolean hasEffect();
}
