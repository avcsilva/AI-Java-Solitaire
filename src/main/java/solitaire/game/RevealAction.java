package solitaire.game;

/** Turns the top card of a column face up (RF19); undoing hides it again. */
final class RevealAction implements Action {

    private final Column column;

    RevealAction(Column column) {
        this.column = column;
    }

    @Override
    public void execute() {
        column.revealTop();
    }

    @Override
    public void undo() {
        column.hideTop();
    }

    @Override
    public boolean hasEffect() {
        return true;
    }
}
