package solitaire.game;

import java.util.List;
import solitaire.model.Card;

/** Moves a card, or a tableau sequence as a single unit, between two locations (RF18). */
final class MoveAction implements Action {

    private final Pile source;
    private final Pile target;
    private final List<Card> cards;

    MoveAction(Pile source, Pile target, List<Card> cards) {
        this.source = source;
        this.target = target;
        this.cards = List.copyOf(cards);
    }

    @Override
    public void execute() {
        transfer(source, target);
    }

    @Override
    public void undo() {
        transfer(target, source);
    }

    private void transfer(Pile from, Pile to) {
        List<Card> removed = from.removeTop(cards.size());
        if (!removed.equals(cards)) {
            throw new IllegalStateException("Unexpected cards on top of the source: " + removed);
        }
        to.add(removed);
    }

    @Override
    public boolean hasEffect() {
        return true;
    }
}
