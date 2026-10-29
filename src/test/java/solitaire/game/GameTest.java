package solitaire.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static solitaire.game.GameFixtures.card;
import static solitaire.game.GameFixtures.snapshot;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import solitaire.model.Card;
import solitaire.strategy.PriorityStrategy;

class GameTest {

    @Test
    void initialDeal() {
        Game game = new Game(new Random(42), new PriorityStrategy());
        assertEquals(24, game.stock().cards().size());
        assertTrue(game.discard().isEmpty());
        game.foundations().forEach(f -> assertTrue(f.isEmpty()));
        int tableau = 0;
        for (int i = 0; i < Game.COLUMN_COUNT; i++) {
            Location column = game.columns().get(i);
            List<Card> cards = column.cards();
            assertEquals(i + 1, cards.size());
            for (int j = 0; j < cards.size(); j++) {
                assertEquals(j == cards.size() - 1, column.isFaceUp(cards.get(j)));
            }
            tableau += cards.size();
        }
        assertEquals(28, tableau);
        assertEquals(0, game.score());
        assertFalse(game.canUndo());
    }

    @Test
    void drawAndUndoReturnSameCard() {
        Game game = new Game(new Random(7), new PriorityStrategy());
        Card top = game.stock().peek();
        assertTrue(game.play(Move.draw()));
        assertSame(top, game.discard().peek());
        assertTrue(game.discard().isFaceUp(top));
        assertTrue(game.undo());
        assertSame(top, game.stock().peek());
        assertTrue(game.discard().isEmpty());
    }

    @Test
    void foundationRules() {
        Game game = new GameFixtures()
                .discard("2H")
                .column(0, List.of(), List.of("AH"))
                .column(1, List.of(), List.of("3H"))
                .build();
        Location f0 = game.foundations().get(0);
        assertFalse(game.canMove(card("2H"), f0));
        assertTrue(game.canMove(card("AH"), f0));
        assertTrue(game.play(Move.transfer(card("AH"), f0)));
        assertTrue(game.canMove(card("2H"), f0));
        assertFalse(game.canMove(card("3H"), f0));
        assertEquals(1, game.score());
    }

    @Test
    void foundationAcceptsOneCardAtATime() {
        Game game = new GameFixtures()
                .foundation(0, "AS")
                .column(0, List.of(), List.of("3H", "2S"))
                .build();
        assertFalse(game.canMove(card("3H"), game.foundations().get(0)));
        assertTrue(game.canMove(card("2S"), game.foundations().get(0)));
    }

    @Test
    void columnRules() {
        Game game = new GameFixtures()
                .discard("KD")
                .column(0, List.of(), List.of("7H"))
                .column(1, List.of(), List.of("6S"))
                .column(2, List.of(), List.of("6D"))
                .build();
        Location c0 = game.columns().get(0);
        Location empty = game.columns().get(3);
        assertTrue(game.canMove(card("6S"), c0));
        assertFalse(game.canMove(card("6D"), c0));
        assertTrue(game.canMove(card("KD"), empty));
        assertFalse(game.canMove(card("6S"), empty));
    }

    @Test
    void discardAndStockAreNotTargets() {
        Game game = new GameFixtures()
                .stock("2C")
                .discard("QH")
                .column(0, List.of(), List.of("KS"))
                .build();
        assertFalse(game.canMove(card("KS"), game.discard()));
        assertFalse(game.canMove(card("KS"), game.stock()));
        assertFalse(game.canMove(card("2C"), game.columns().get(1)));
    }

    @Test
    void sequenceMovesTogetherAndRevealIsUndone() {
        Game game = new GameFixtures()
                .column(0, List.of("2C"), List.of("8H", "7S", "6D"))
                .column(1, List.of(), List.of("9C"))
                .build();
        String before = snapshot(game);
        assertEquals(List.of(card("8H"), card("7S"), card("6D")), game.sequenceFrom(card("8H")));
        assertTrue(game.play(Move.transfer(card("8H"), game.columns().get(1))));
        assertEquals(List.of(card("9C"), card("8H"), card("7S"), card("6D")), game.columns().get(1).cards());
        assertTrue(game.isFaceUp(card("2C")));
        assertTrue(game.undo());
        assertFalse(game.isFaceUp(card("2C")));
        assertEquals(before, snapshot(game));
        assertFalse(game.canUndo());
    }

    @Test
    void illegalMoveChangesNothingAndDoesNotNotify() {
        Game game = new GameFixtures()
                .column(0, List.of(), List.of("8H"))
                .column(1, List.of(), List.of("9H"))
                .build();
        AtomicInteger notifications = new AtomicInteger();
        game.addListener(notifications::incrementAndGet);
        String before = snapshot(game);
        assertFalse(game.play(Move.transfer(card("8H"), game.columns().get(1))));
        assertFalse(game.play(Move.none()));
        assertFalse(game.play(Move.draw()));
        assertEquals(before, snapshot(game));
        assertEquals(0, notifications.get());
        assertFalse(game.canUndo());
    }

    @Test
    void changesAndUndoNotify() {
        Game game = new Game(new Random(3), new PriorityStrategy());
        AtomicInteger notifications = new AtomicInteger();
        game.addListener(notifications::incrementAndGet);
        game.play(Move.draw());
        game.undo();
        assertEquals(2, notifications.get());
    }

    @Test
    void queriesDoNotChangeState() {
        Game game = new Game(new Random(11), new PriorityStrategy());
        String before = snapshot(game);
        for (Card card : Card.all()) {
            game.locationOf(card);
            game.sequenceFrom(card);
            game.isFaceUp(card);
            game.isLowestFaceUp(card);
            game.isKingAtBase(card);
            for (Location column : game.columns()) {
                game.canMove(card, column);
            }
            for (Location foundation : game.foundations()) {
                game.canMove(card, foundation);
            }
        }
        game.score();
        game.isWon();
        new PriorityStrategy().suggest(game);
        assertEquals(before, snapshot(game));
        assertFalse(game.canUndo());
    }

    @Test
    void locationOfOnlyForAccessibleCards() {
        Game game = new GameFixtures()
                .stock("2C")
                .discard("3D", "QH")
                .column(0, List.of("5S"), List.of("KS", "QD"))
                .build();
        assertTrue(game.locationOf(card("2C")).isEmpty());
        assertTrue(game.locationOf(card("3D")).isEmpty());
        assertTrue(game.locationOf(card("5S")).isEmpty());
        assertSame(game.discard(), game.locationOf(card("QH")).orElseThrow());
        assertSame(game.columns().get(0), game.locationOf(card("KS")).orElseThrow());
        assertTrue(game.isLowestFaceUp(card("KS")));
        assertFalse(game.isKingAtBase(card("KS")));
    }

    @Test
    void kingAtBase() {
        Game game = new GameFixtures().column(0, List.of(), List.of("KS", "QD")).build();
        assertTrue(game.isKingAtBase(card("KS")));
    }

    @Test
    void drawFromEmptyStockStartsNewGame() {
        Game game = new GameFixtures().discard("QH").build();
        game.drawFromStock();
        assertEquals(24, game.stock().cards().size());
        assertTrue(game.discard().isEmpty());
        assertFalse(game.canUndo());
    }

    @Test
    void winDetection() {
        GameFixtures fixtures = new GameFixtures()
                .foundation(0, "KH")
                .foundation(1, "KD")
                .foundation(2, "KC")
                .foundation(3, "QS")
                .column(0, List.of(), List.of("KS"));
        Game game = fixtures.build();
        assertFalse(game.isWon());
        assertTrue(game.play(Move.transfer(card("KS"), game.foundations().get(3))));
        assertEquals(52, game.score());
        assertTrue(game.isWon());
    }
}
