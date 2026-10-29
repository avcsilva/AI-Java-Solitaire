package solitaire.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A playing card. There is exactly one instance per rank and suit, so every reference
 * to a given card is the same object (RF04).
 */
public final class Card {

    private static final Card[][] CARDS = new Card[Suit.values().length][Rank.values().length];
    private static final List<Card> ALL;

    static {
        List<Card> all = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                Card card = new Card(rank, suit);
                CARDS[suit.ordinal()][rank.ordinal()] = card;
                all.add(card);
            }
        }
        ALL = Collections.unmodifiableList(all);
    }

    private final Rank rank;
    private final Suit suit;

    private Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public static Card of(Rank rank, Suit suit) {
        return CARDS[suit.ordinal()][rank.ordinal()];
    }

    /** All 52 cards, ordered by suit and rank. */
    public static List<Card> all() {
        return ALL;
    }

    /** Parses an identifier produced by {@link #id()}, returning the unique card instance. */
    public static Card fromId(String id) {
        if (id == null || id.length() < 2) {
            throw new IllegalArgumentException("Invalid card id: " + id);
        }
        Rank rank = Rank.fromLabel(id.substring(0, id.length() - 1));
        Suit suit = Suit.fromCode(id.charAt(id.length() - 1));
        return of(rank, suit);
    }

    public Rank rank() {
        return rank;
    }

    public Suit suit() {
        return suit;
    }

    public SuitColor color() {
        return suit.color();
    }

    /** Compact textual identifier, such as "10H" or "QS". */
    public String id() {
        return rank.label() + suit.code();
    }

    @Override
    public String toString() {
        return id();
    }
}
