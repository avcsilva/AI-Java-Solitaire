package solitaire.game;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import solitaire.model.Card;
import solitaire.model.Rank;

/**
 * A tableau column (RF16, RF17). Visibility is tracked per card, so it always reflects the card
 * actually at each position (RNF01).
 */
final class Column extends Pile {

    private final Set<Card> faceUp = Collections.newSetFromMap(new IdentityHashMap<>());

    @Override
    public boolean isFaceUp(Card card) {
        return faceUp.contains(card);
    }

    @Override
    public boolean accepts(List<Card> sequence) {
        if (sequence.isEmpty()) {
            return false;
        }
        Card first = sequence.get(0);
        Card top = peek();
        if (top == null) {
            return first.rank() == Rank.KING;
        }
        return first.rank().isImmediatelyBelow(top.rank()) && !first.suit().hasSameColorAs(top.suit());
    }

    /** The card and every card on top of it, when it is face up (RF18, RF20). */
    @Override
    List<Card> movableSequenceFrom(Card card) {
        if (!isFaceUp(card)) {
            return List.of();
        }
        List<Card> all = stack.cards();
        return all.subList(stack.indexOf(card), all.size());
    }

    /** The face-up card closest to the base, or {@code null} when there is none (RF20). */
    Card lowestFaceUp() {
        for (Card card : stack.cards()) {
            if (isFaceUp(card)) {
                return card;
            }
        }
        return null;
    }

    boolean isAtBase(Card card) {
        return stack.indexOf(card) == 0;
    }

    void addHidden(Card card) {
        stack.push(card);
    }

    /** Cards moved into a column are always face up. */
    @Override
    void add(List<Card> sequence) {
        super.add(sequence);
        faceUp.addAll(sequence);
    }

    @Override
    List<Card> removeTop(int count) {
        List<Card> removed = super.removeTop(count);
        removed.forEach(faceUp::remove);
        return removed;
    }

    @Override
    Action followUpAfterRemoving(int count) {
        int belowIndex = size() - count - 1;
        if (belowIndex < 0 || isFaceUp(stack.cards().get(belowIndex))) {
            return NullAction.INSTANCE;
        }
        return new RevealAction(this);
    }

    void revealTop() {
        faceUp.add(peek());
    }

    void hideTop() {
        faceUp.remove(peek());
    }

    @Override
    void clear() {
        super.clear();
        faceUp.clear();
    }
}
