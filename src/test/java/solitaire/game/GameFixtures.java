package solitaire.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import solitaire.model.Card;
import solitaire.model.Rank;
import solitaire.strategy.PriorityStrategy;

/** Builds games in specific states for tests. */
public final class GameFixtures {

    private final Game game = new Game(new Random(0), new PriorityStrategy());

    public GameFixtures() {
        game.clearBoard();
    }

    public static Card card(String id) {
        return Card.fromId(id);
    }

    public GameFixtures stock(String... ids) {
        for (String id : ids) {
            game.stockPile().add(List.of(card(id)));
        }
        return this;
    }

    public GameFixtures discard(String... ids) {
        for (String id : ids) {
            game.discardPile().add(List.of(card(id)));
        }
        return this;
    }

    /** Fills a foundation from Ace up to {@code top}, in the suit of {@code top}. */
    public GameFixtures foundation(int index, String top) {
        Card last = card(top);
        for (Rank rank : Rank.values()) {
            game.foundationPile(index).add(List.of(Card.of(rank, last.suit())));
            if (rank == last.rank()) {
                break;
            }
        }
        return this;
    }

    /** Arranges a column: hidden cards first, then face-up cards. */
    public GameFixtures column(int index, List<String> hidden, List<String> faceUp) {
        Column column = game.columnPile(index);
        for (String id : hidden) {
            column.addHidden(card(id));
        }
        List<Card> visible = new ArrayList<>();
        for (String id : faceUp) {
            visible.add(card(id));
        }
        column.add(visible);
        return this;
    }

    public Game build() {
        return game;
    }

    /** Snapshot of the full visible and hidden state, for equality checks. */
    public static String snapshot(GameView game) {
        StringBuilder text = new StringBuilder();
        List<Location> locations = new ArrayList<>();
        locations.add(game.stock());
        locations.add(game.discard());
        locations.addAll(game.foundations());
        locations.addAll(game.columns());
        for (Location location : locations) {
            for (Card card : location.cards()) {
                text.append(card.id()).append(location.isFaceUp(card) ? "+" : "-").append(' ');
            }
            text.append('|');
        }
        return text.toString();
    }
}
