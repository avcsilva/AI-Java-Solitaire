package solitaire.game;

import java.util.List;
import solitaire.model.Card;
import solitaire.model.CardStack;

/** Common implementation of every location, backed by a {@link CardStack} (RF08). */
abstract class Pile implements Location {

    protected final CardStack stack = new CardStack();

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public Card peek() {
        return stack.peek();
    }

    @Override
    public List<Card> cards() {
        return stack.cards();
    }

    @Override
    public boolean contains(Card card) {
        return stack.indexOf(card) >= 0;
    }

    @Override
    public boolean isFaceUp(Card card) {
        return contains(card);
    }

    int size() {
        return stack.size();
    }

    /** Cards that would leave this pile together with {@code card}; empty when it cannot be moved. */
    List<Card> movableSequenceFrom(Card card) {
        return card == stack.peek() ? List.of(card) : List.of();
    }

    void add(List<Card> sequence) {
        stack.pushAll(sequence);
    }

    List<Card> removeTop(int count) {
        return stack.removeFrom(stack.size() - count);
    }

    /** Action that must accompany removing {@code count} cards from the top (RF19). */
    Action followUpAfterRemoving(int count) {
        return NullAction.INSTANCE;
    }

    void clear() {
        stack.clear();
    }
}
