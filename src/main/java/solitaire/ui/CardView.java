package solitaire.ui;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import solitaire.model.Card;
import solitaire.model.SuitColor;

/** A card drawn entirely by the application, face up or face down. */
final class CardView extends Canvas {

    static final double WIDTH = 80;
    static final double HEIGHT = 112;
    private static final double ARC = 12;

    private final Card card;

    CardView(Card card, boolean faceUp) {
        super(WIDTH, HEIGHT);
        this.card = card;
        if (faceUp) {
            drawFace();
        } else {
            drawBack();
        }
    }

    Card card() {
        return card;
    }

    private void drawFace() {
        GraphicsContext g = getGraphicsContext2D();
        g.setFill(Color.WHITE);
        g.fillRoundRect(0.5, 0.5, WIDTH - 1, HEIGHT - 1, ARC, ARC);
        g.setStroke(Color.web("#888888"));
        g.setLineWidth(1);
        g.strokeRoundRect(0.5, 0.5, WIDTH - 1, HEIGHT - 1, ARC, ARC);

        Color ink = card.color() == SuitColor.RED ? Color.web("#c62828") : Color.web("#212121");
        g.setFill(ink);
        g.setTextBaseline(VPos.TOP);

        String rank = card.rank().label();
        String suit = card.suit().symbol();

        g.setTextAlign(TextAlignment.LEFT);
        g.setFont(Font.font("System", FontWeight.BOLD, 16));
        g.fillText(rank, 6, 4);
        g.setFont(Font.font("System", 14));
        g.fillText(suit, 6, 22);

        g.save();
        g.translate(WIDTH, HEIGHT);
        g.rotate(180);
        g.setFont(Font.font("System", FontWeight.BOLD, 16));
        g.fillText(rank, 6, 4);
        g.setFont(Font.font("System", 14));
        g.fillText(suit, 6, 22);
        g.restore();

        g.setTextAlign(TextAlignment.CENTER);
        g.setTextBaseline(VPos.CENTER);
        g.setFont(Font.font("System", 40));
        g.fillText(suit, WIDTH / 2, HEIGHT / 2);
    }

    private void drawBack() {
        GraphicsContext g = getGraphicsContext2D();
        g.setFill(Color.WHITE);
        g.fillRoundRect(0.5, 0.5, WIDTH - 1, HEIGHT - 1, ARC, ARC);
        g.setStroke(Color.web("#888888"));
        g.strokeRoundRect(0.5, 0.5, WIDTH - 1, HEIGHT - 1, ARC, ARC);

        double inset = 5;
        double w = WIDTH - 2 * inset;
        double h = HEIGHT - 2 * inset;
        g.setFill(Color.web("#1f4e8c"));
        g.fillRoundRect(inset, inset, w, h, ARC - 4, ARC - 4);

        g.save();
        g.beginPath();
        g.rect(inset, inset, w, h);
        g.clip();
        g.setStroke(Color.web("#4f7fc0"));
        g.setLineWidth(1);
        double step = 8;
        for (double d = -h; d < w + h; d += step) {
            g.strokeLine(inset + d, inset, inset + d + h, inset + h);
            g.strokeLine(inset + d, inset + h, inset + d + h, inset);
        }
        g.restore();
    }
}
