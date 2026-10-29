package solitaire.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static solitaire.game.GameFixtures.card;

import java.util.List;
import org.junit.jupiter.api.Test;
import solitaire.strategy.DisabledStrategy;
import solitaire.strategy.PriorityStrategy;

class PriorityStrategyTest {

    private final PriorityStrategy strategy = new PriorityStrategy();

    @Test
    void priority1DrawWhenDiscardEmpty() {
        Game game = new GameFixtures()
                .stock("2C")
                .column(0, List.of(), List.of("AH"))
                .build();
        assertEquals(Move.draw(), strategy.suggest(game));
    }

    @Test
    void priority2DiscardToFoundation() {
        Game game = new GameFixtures()
                .stock("2C")
                .discard("AD")
                .column(0, List.of(), List.of("AH"))
                .column(1, List.of(), List.of("2S"))
                .build();
        assertEquals(Move.transfer(card("AD"), game.foundations().get(0)), strategy.suggest(game));
    }

    @Test
    void priority3DiscardToTableau() {
        Game game = new GameFixtures()
                .discard("QH")
                .column(0, List.of(), List.of("AH"))
                .column(1, List.of(), List.of("KS"))
                .build();
        assertEquals(Move.transfer(card("QH"), game.columns().get(1)), strategy.suggest(game));
    }

    @Test
    void priority4ColumnToFoundation() {
        Game game = new GameFixtures()
                .discard("5H")
                .column(0, List.of(), List.of("9C"))
                .column(1, List.of("3C"), List.of("AH"))
                .build();
        assertEquals(Move.transfer(card("AH"), game.foundations().get(0)), strategy.suggest(game));
    }

    @Test
    void priority5RevealsHiddenCard() {
        Game game = new GameFixtures()
                .stock("2C")
                .discard("5H")
                .column(0, List.of(), List.of("9C"))
                .column(1, List.of("3C"), List.of("8H", "7S"))
                .build();
        assertEquals(Move.transfer(card("8H"), game.columns().get(0)), strategy.suggest(game));
    }

    @Test
    void priority5IgnoresMovesWithoutGain() {
        // 8H could go on 9S, but it is not the lowest face-up card of its column.
        Game game = new GameFixtures()
                .stock("2C")
                .discard("5H")
                .column(0, List.of(), List.of("9S"))
                .column(1, List.of("3C"), List.of("9C", "8H"))
                .build();
        assertEquals(Move.draw(), strategy.suggest(game));
    }

    @Test
    void kingAtBaseIsNotMovedToEmptyColumn() {
        Game game = new GameFixtures()
                .discard("5H")
                .column(0, List.of(), List.of("KS", "QH"))
                .build();
        assertEquals(Move.none(), strategy.suggest(game));
    }

    @Test
    void priority6DrawWhenNothingElse() {
        Game game = new GameFixtures()
                .stock("2C")
                .discard("5H")
                .column(0, List.of(), List.of("9C"))
                .build();
        assertEquals(Move.draw(), strategy.suggest(game));
    }

    @Test
    void priority7NoMove() {
        Game game = new GameFixtures()
                .discard("5H")
                .column(0, List.of(), List.of("9C"))
                .build();
        assertEquals(Move.none(), strategy.suggest(game));
        assertFalse(game.autoPlay());
    }

    @Test
    void disabledStrategyNeverPlays() {
        Game game = new GameFixtures().stock("2C").build();
        game.setStrategy(new DisabledStrategy());
        assertFalse(game.autoPlay());
        assertFalse(game.canUndo());
    }
}
