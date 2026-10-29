package solitaire.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CardTest {

    @Test
    void deckHas52UniqueCards() {
        Set<Card> unique = Collections.newSetFromMap(new IdentityHashMap<>());
        unique.addAll(Card.all());
        assertEquals(52, Card.all().size());
        assertEquals(52, unique.size());
    }

    @Test
    void sameRankAndSuitIsSameInstance() {
        for (Card card : Card.all()) {
            assertSame(card, Card.of(card.rank(), card.suit()));
            assertSame(card, Card.fromId(card.id()));
        }
    }

    @Test
    void ranksHaveTotalOrder() {
        Rank[] ranks = Rank.values();
        assertEquals(Rank.ACE, ranks[0]);
        assertEquals(Rank.KING, ranks[12]);
        for (int i = 1; i < ranks.length; i++) {
            assertTrue(ranks[i].isImmediatelyAbove(ranks[i - 1]));
            assertTrue(ranks[i - 1].isImmediatelyBelow(ranks[i]));
        }
        assertFalse(Rank.SEVEN.isImmediatelyAbove(Rank.FIVE));
        assertFalse(Rank.ACE.isImmediatelyAbove(Rank.KING));
    }

    @Test
    void suitColors() {
        assertEquals(SuitColor.RED, Suit.HEARTS.color());
        assertEquals(SuitColor.RED, Suit.DIAMONDS.color());
        assertEquals(SuitColor.BLACK, Suit.CLUBS.color());
        assertEquals(SuitColor.BLACK, Suit.SPADES.color());
        assertTrue(Suit.HEARTS.hasSameColorAs(Suit.DIAMONDS));
        assertFalse(Suit.HEARTS.hasSameColorAs(Suit.SPADES));
    }

    @Test
    void shuffleKeepsAllCardsInDifferentOrders() {
        List<Card> first = Deck.shuffled(new Random(1));
        List<Card> second = Deck.shuffled(new Random(2));
        assertEquals(Set.copyOf(Card.all()), Set.copyOf(first));
        assertNotEquals(first, second);
    }

    @Test
    void serializationPreservesIdentityAndOrder() {
        List<Card> sequence = List.of(
                Card.of(Rank.EIGHT, Suit.HEARTS),
                Card.of(Rank.SEVEN, Suit.SPADES),
                Card.of(Rank.TEN, Suit.DIAMONDS),
                Card.of(Rank.SIX, Suit.CLUBS));
        List<Card> restored = CardSerializer.deserialize(CardSerializer.serialize(sequence));
        assertEquals(sequence.size(), restored.size());
        for (int i = 0; i < sequence.size(); i++) {
            assertSame(sequence.get(i), restored.get(i));
        }
        List<Card> all = CardSerializer.deserialize(CardSerializer.serialize(Card.all()));
        for (int i = 0; i < all.size(); i++) {
            assertSame(Card.all().get(i), all.get(i));
        }
    }

    @Test
    void stackKeepsOrderAndTop() {
        CardStack stack = new CardStack();
        assertTrue(stack.isEmpty());
        Card a = Card.of(Rank.ACE, Suit.CLUBS);
        Card b = Card.of(Rank.TWO, Suit.CLUBS);
        stack.push(a);
        stack.push(b);
        assertSame(b, stack.peek());
        assertEquals(List.of(a, b), stack.cards());
        assertSame(b, stack.pop());
        assertSame(a, stack.peek());
    }
}
