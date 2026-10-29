package solitaire.game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import solitaire.model.Card;
import solitaire.model.Deck;
import solitaire.model.Rank;
import solitaire.strategy.Strategy;

/**
 * Central game engine: holds the state, turns suggested moves into legal actions (RF21),
 * executes them, keeps the history (RF25) and notifies listeners (RF26).
 */
public final class Game implements GameView {

    public static final int COLUMN_COUNT = 7;
    public static final int FOUNDATION_COUNT = 4;

    private final Random random;
    private final Stock stock = new Stock();
    private final Discard discard = new Discard();
    private final List<Foundation> foundations = new ArrayList<>();
    private final List<Column> columns = new ArrayList<>();
    private final List<Pile> piles = new ArrayList<>();
    private final Deque<Action> history = new ArrayDeque<>();
    private final List<GameListener> listeners = new ArrayList<>();
    private Strategy strategy;

    public Game(Random random, Strategy strategy) {
        this.random = random;
        this.strategy = strategy;
        for (int i = 0; i < FOUNDATION_COUNT; i++) {
            foundations.add(new Foundation());
        }
        for (int i = 0; i < COLUMN_COUNT; i++) {
            columns.add(new Column());
        }
        piles.add(stock);
        piles.add(discard);
        piles.addAll(foundations);
        piles.addAll(columns);
        deal();
    }

    /** Shuffles and deals a new game: 28 cards in the tableau, 24 in the stock (RF01, RF16). */
    public void newGame() {
        deal();
        notifyListeners();
    }

    private void deal() {
        piles.forEach(Pile::clear);
        history.clear();
        List<Card> deck = Deck.shuffled(random);
        int next = 0;
        for (int i = 0; i < COLUMN_COUNT; i++) {
            Column column = columns.get(i);
            for (int j = 0; j <= i; j++) {
                column.addHidden(deck.get(next++));
            }
            column.revealTop();
        }
        stock.add(deck.subList(next, deck.size()));
    }

    /** The player's draw: draws a card, or starts a new game when the stock is empty (RF01, RF11). */
    public void drawFromStock() {
        if (stock.isEmpty()) {
            newGame();
        } else {
            play(Move.draw());
        }
    }

    /** Checks and executes a suggested move. Returns whether the state changed. */
    public boolean play(Move move) {
        return perform(toAction(move));
    }

    /** Lets the current strategy suggest a move and executes it like any other move (RF28, RF31). */
    public boolean autoPlay() {
        return play(strategy.suggest(this));
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    /** Undoes the last play, if any (RF25). */
    public boolean undo() {
        if (history.isEmpty()) {
            return false;
        }
        history.pop().undo();
        notifyListeners();
        return true;
    }

    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameListener listener) {
        listeners.remove(listener);
    }

    private boolean perform(Action action) {
        if (!action.hasEffect()) {
            return false;
        }
        action.execute();
        history.push(action);
        notifyListeners();
        return true;
    }

    private void notifyListeners() {
        for (GameListener listener : List.copyOf(listeners)) {
            listener.onGameChanged();
        }
    }

    private Action toAction(Move move) {
        return switch (move) {
            case Move.Draw draw -> stock.isEmpty() ? NullAction.INSTANCE : new DrawAction(stock, discard);
            case Move.Transfer transfer -> transferAction(transfer.card(), transfer.target());
            case Move.None none -> NullAction.INSTANCE;
        };
    }

    private Action transferAction(Card card, Location target) {
        Optional<Pile> source = accessiblePileOf(card);
        Optional<Pile> destination = pileOf(target);
        if (source.isEmpty() || destination.isEmpty() || source.get() == destination.get()) {
            return NullAction.INSTANCE;
        }
        List<Card> sequence = source.get().movableSequenceFrom(card);
        if (!destination.get().accepts(sequence)) {
            return NullAction.INSTANCE;
        }
        return new CompositeAction(
                new MoveAction(source.get(), destination.get(), sequence),
                source.get().followUpAfterRemoving(sequence.size()));
    }

    private Optional<Pile> pileOf(Location location) {
        for (Pile pile : piles) {
            if (pile == location) {
                return Optional.of(pile);
            }
        }
        return Optional.empty();
    }

    private Optional<Pile> containingPile(Card card) {
        for (Pile pile : piles) {
            if (pile.contains(card)) {
                return Optional.of(pile);
            }
        }
        return Optional.empty();
    }

    private Optional<Pile> accessiblePileOf(Card card) {
        return containingPile(card).filter(pile -> !pile.movableSequenceFrom(card).isEmpty());
    }

    private Optional<Column> columnOf(Card card) {
        for (Column column : columns) {
            if (column.contains(card)) {
                return Optional.of(column);
            }
        }
        return Optional.empty();
    }

    @Override
    public Location stock() {
        return stock;
    }

    @Override
    public Location discard() {
        return discard;
    }

    @Override
    public List<Location> foundations() {
        return List.copyOf(foundations);
    }

    @Override
    public List<Location> columns() {
        return List.copyOf(columns);
    }

    @Override
    public Optional<Location> locationOf(Card card) {
        return accessiblePileOf(card).map(Location.class::cast);
    }

    @Override
    public List<Card> sequenceFrom(Card card) {
        return containingPile(card).map(pile -> pile.movableSequenceFrom(card)).orElse(List.of());
    }

    @Override
    public boolean isFaceUp(Card card) {
        return containingPile(card).map(pile -> pile.isFaceUp(card)).orElse(false);
    }

    @Override
    public boolean isLowestFaceUp(Card card) {
        return columnOf(card).map(column -> column.lowestFaceUp() == card).orElse(false);
    }

    @Override
    public boolean isKingAtBase(Card card) {
        return card.rank() == Rank.KING
                && columnOf(card).map(column -> column.isAtBase(card) && column.isFaceUp(card)).orElse(false);
    }

    @Override
    public boolean canMove(Card card, Location target) {
        return transferAction(card, target).hasEffect();
    }

    @Override
    public int score() {
        int score = 0;
        for (Foundation foundation : foundations) {
            score += foundation.size();
        }
        return score;
    }

    @Override
    public boolean isWon() {
        return score() == Card.all().size();
    }

    @Override
    public boolean canUndo() {
        return !history.isEmpty();
    }

    // Package-private access used to arrange specific states in tests.

    Stock stockPile() {
        return stock;
    }

    Discard discardPile() {
        return discard;
    }

    Foundation foundationPile(int index) {
        return foundations.get(index);
    }

    Column columnPile(int index) {
        return columns.get(index);
    }

    void clearBoard() {
        piles.forEach(Pile::clear);
        history.clear();
    }
}
