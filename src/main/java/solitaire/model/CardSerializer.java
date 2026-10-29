package solitaire.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts card sequences to text and back (e.g. for drag and drop). Deserializing returns
 * the same card instances in the same order (RNF05).
 */
public final class CardSerializer {

    private static final String SEPARATOR = ",";

    private CardSerializer() {
    }

    public static String serialize(List<Card> cards) {
        List<String> ids = new ArrayList<>(cards.size());
        for (Card card : cards) {
            ids.add(card.id());
        }
        return String.join(SEPARATOR, ids);
    }

    public static List<Card> deserialize(String text) {
        List<Card> cards = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return cards;
        }
        for (String id : text.split(SEPARATOR)) {
            cards.add(Card.fromId(id));
        }
        return cards;
    }
}
