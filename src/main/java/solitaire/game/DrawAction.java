package solitaire.game;

import java.util.List;
import solitaire.model.Card;

/** Moves the top card of the stock, face up, to the discard (RF11); undoing returns it (RF12). */
final class DrawAction implements Action {

    private final Stock stock;
    private final Discard discard;
    private Card drawn;

    DrawAction(Stock stock, Discard discard) {
        this.stock = stock;
        this.discard = discard;
    }

    @Override
    public void execute() {
        drawn = stock.removeTop(1).get(0);
        discard.add(List.of(drawn));
    }

    @Override
    public void undo() {
        Card card = discard.removeTop(1).get(0);
        if (card != drawn) {
            throw new IllegalStateException("Discard top is not the drawn card");
        }
        stock.add(List.of(card));
    }

    @Override
    public boolean hasEffect() {
        return true;
    }
}
