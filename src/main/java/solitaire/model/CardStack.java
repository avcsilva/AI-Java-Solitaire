package solitaire.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Ordered pile of cards, from base to top (RF08). Only the top is played or removed directly;
 * {@link #removeFrom(int)} and {@link #pushAll(List)} exist for tableau sequences (RF18).
 */
public final class CardStack {

    private final List<Card> cards = new ArrayList<>();

    public void push(Card card) {
        cards.add(card);
    }

    public void pushAll(List<Card> sequence) {
        cards.addAll(sequence);
    }

    public Card pop() {
        if (cards.isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return cards.remove(cards.size() - 1);
    }

    /** Removes and returns the cards from {@code index} to the top, in the same order. */
    public List<Card> removeFrom(int index) {
        List<Card> tail = cards.subList(index, cards.size());
        List<Card> removed = List.copyOf(tail);
        tail.clear();
        return removed;
    }

    /** Top card, or {@code null} when the stack is empty. */
    public Card peek() {
        return cards.isEmpty() ? null : cards.get(cards.size() - 1);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }

    /** Index of the card from the base, or -1 when absent. Compared by identity. */
    public int indexOf(Card card) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) == card) {
                return i;
            }
        }
        return -1;
    }

    /** Immutable snapshot of the cards, from base to top. */
    public List<Card> cards() {
        return List.copyOf(cards);
    }

    public void clear() {
        cards.clear();
    }
}
