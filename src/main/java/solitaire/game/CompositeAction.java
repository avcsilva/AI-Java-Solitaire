package solitaire.game;

import java.util.List;

/** Several related actions treated as a single play (RF23). */
final class CompositeAction implements Action {

    private final List<Action> actions;

    CompositeAction(Action... actions) {
        this.actions = List.of(actions);
    }

    @Override
    public void execute() {
        for (Action action : actions) {
            action.execute();
        }
    }

    @Override
    public void undo() {
        for (int i = actions.size() - 1; i >= 0; i--) {
            actions.get(i).undo();
        }
    }

    @Override
    public boolean hasEffect() {
        for (Action action : actions) {
            if (action.hasEffect()) {
                return true;
            }
        }
        return false;
    }
}
