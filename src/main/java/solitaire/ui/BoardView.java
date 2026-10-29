package solitaire.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import solitaire.game.Game;
import solitaire.game.Location;
import solitaire.game.Move;
import solitaire.model.Card;
import solitaire.model.CardSerializer;

/**
 * Displays the board and turns mouse input into moves. It holds no game rules: legality is
 * always asked to the game (RF21, RNF03). The whole board is redrawn from the game state on
 * every notification, so what is shown always matches the current state (RNF01).
 */
final class BoardView extends VBox {

    private static final double GAP = 16;
    private static final double HIDDEN_OFFSET = 14;
    private static final double VISIBLE_OFFSET = 26;
    private static final double COLUMN_HEIGHT = 520;
    private static final String ACCEPT_STYLE = "-fx-background-color: rgba(120, 255, 120, 0.45); -fx-background-radius: 10;";
    private static final String REJECT_STYLE = "-fx-background-color: rgba(255, 90, 90, 0.45); -fx-background-radius: 10;";

    private final Game game;
    private final List<Slot> slots = new ArrayList<>();
    private final List<CardView> dragged = new ArrayList<>();

    BoardView(Game game) {
        this.game = game;
        setSpacing(GAP);
        setPadding(new Insets(GAP));

        Slot stockSlot = new Slot(game.stock(), false, "↻");
        stockSlot.setOnMouseClicked(event -> game.drawFromStock());

        HBox topRow = new HBox(GAP);
        topRow.getChildren().add(stockSlot);
        topRow.getChildren().add(new Slot(game.discard(), false, ""));
        Region spacer = new Region();
        spacer.setMinWidth(CardView.WIDTH + GAP);
        topRow.getChildren().add(spacer);
        for (Location foundation : game.foundations()) {
            topRow.getChildren().add(new Slot(foundation, false, "A"));
        }

        HBox tableau = new HBox(GAP);
        for (Location column : game.columns()) {
            tableau.getChildren().add(new Slot(column, true, "K"));
        }

        getChildren().addAll(topRow, tableau);
        game.addListener(this::refresh);
        refresh();
    }

    private void refresh() {
        dragged.clear();
        slots.forEach(Slot::render);
    }

    /** The area of one location: an outline plus the cards currently in it. */
    private final class Slot extends Pane {

        private final Location location;
        private final boolean fanned;
        private final Canvas placeholder;

        Slot(Location location, boolean fanned, String hint) {
            this.location = location;
            this.fanned = fanned;
            this.placeholder = createPlaceholder(hint);
            double height = fanned ? COLUMN_HEIGHT : CardView.HEIGHT;
            setMinSize(CardView.WIDTH, height);
            setPrefSize(CardView.WIDTH, height);
            setMaxSize(CardView.WIDTH, height);
            setOnDragOver(this::onDragOver);
            setOnDragExited(event -> setStyle(""));
            setOnDragDropped(this::onDragDropped);
            slots.add(this);
        }

        void render() {
            setStyle("");
            getChildren().setAll(placeholder);
            double y = 0;
            for (Card card : location.cards()) {
                boolean faceUp = location.isFaceUp(card);
                CardView view = new CardView(card, faceUp);
                view.relocate(0, fanned ? y : 0);
                view.setOnDragDetected(event -> startDrag(view));
                getChildren().add(view);
                y += faceUp ? VISIBLE_OFFSET : HIDDEN_OFFSET;
            }
        }

        private void startDrag(CardView view) {
            Card card = view.card();
            if (game.locationOf(card).isEmpty()) {
                return;
            }
            List<Card> sequence = game.sequenceFrom(card);
            Dragboard dragboard = view.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(CardSerializer.serialize(sequence));
            dragboard.setContent(content);

            Pane image = new Pane();
            double y = 0;
            for (Card moving : sequence) {
                CardView copy = new CardView(moving, true);
                copy.relocate(0, y);
                image.getChildren().add(copy);
                y += VISIBLE_OFFSET;
            }
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            dragboard.setDragView(image.snapshot(parameters, null), CardView.WIDTH / 2, 12);

            for (var node : getChildren()) {
                if (node instanceof CardView cardView && sequence.contains(cardView.card())) {
                    cardView.setOpacity(0.35);
                    dragged.add(cardView);
                }
            }
            view.setOnDragDone(event -> {
                dragged.forEach(cardView -> cardView.setOpacity(1));
                dragged.clear();
            });
        }

        private void onDragOver(DragEvent event) {
            List<Card> cards = draggedCards(event);
            if (cards.isEmpty()) {
                return;
            }
            if (game.canMove(cards.get(0), location)) {
                event.acceptTransferModes(TransferMode.MOVE);
                setStyle(ACCEPT_STYLE);
            } else {
                setStyle(REJECT_STYLE);
            }
            event.consume();
        }

        private void onDragDropped(DragEvent event) {
            List<Card> cards = draggedCards(event);
            boolean success = !cards.isEmpty() && game.play(Move.transfer(cards.get(0), location));
            event.setDropCompleted(success);
            event.consume();
        }

        private List<Card> draggedCards(DragEvent event) {
            Dragboard dragboard = event.getDragboard();
            if (!dragboard.hasString()) {
                return List.of();
            }
            try {
                return CardSerializer.deserialize(dragboard.getString());
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }
    }

    private static Canvas createPlaceholder(String hint) {
        Canvas canvas = new Canvas(CardView.WIDTH, CardView.HEIGHT);
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setStroke(Color.rgb(255, 255, 255, 0.45));
        g.setLineWidth(2);
        g.setLineDashes(6, 4);
        g.strokeRoundRect(1, 1, CardView.WIDTH - 2, CardView.HEIGHT - 2, 12, 12);
        g.setFill(Color.rgb(255, 255, 255, 0.35));
        g.setFont(Font.font("System", 32));
        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.fillText(hint, CardView.WIDTH / 2, CardView.HEIGHT / 2);
        return canvas;
    }
}
