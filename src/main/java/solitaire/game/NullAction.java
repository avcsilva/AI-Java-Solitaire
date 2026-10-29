package solitaire.game;

/** An action without effect, e.g. when no move is available (RF24). */
public final class NullAction implements Action {

    public static final NullAction INSTANCE = new NullAction();

    private NullAction() {
    }

    @Override
    public void execute() {
    }

    @Override
    public void undo() {
    }

    @Override
    public boolean hasEffect() {
        return false;
    }
}
