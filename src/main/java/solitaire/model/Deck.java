package solitaire.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** The full 52-card deck (RF04) and its shuffling (RF07). */
public final class Deck {

    private Deck() {
    }

    /** Returns the 52 cards in a random order. */
    public static List<Card> shuffled(Random random) {
        List<Card> cards = new ArrayList<>(Card.all());
        Collections.shuffle(cards, random);
        return cards;
    }
}
