package solitaire.ui;

import java.util.Random;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import solitaire.game.Game;
import solitaire.strategy.DisabledStrategy;
import solitaire.strategy.PriorityStrategy;

/** Desktop entry point: Klondike Solitaire. */
public final class SolitaireApp extends Application {

    private boolean winAnnounced;

    @Override
    public void start(Stage stage) {
        Game game = new Game(new Random(), new PriorityStrategy());

        Button undoButton = new Button("Desfazer");
        undoButton.setOnAction(event -> game.undo());

        Button autoButton = new Button("Jogada automática");
        autoButton.setOnAction(event -> game.autoPlay());

        CheckBox autoEnabled = new CheckBox("Jogo automático ativado");
        autoEnabled.setSelected(true);
        autoEnabled.setStyle("-fx-text-fill: white;");
        autoEnabled.selectedProperty().addListener((observable, wasSelected, selected) ->
                game.setStrategy(selected ? new PriorityStrategy() : new DisabledStrategy()));

        HBox toolbar = new HBox(12, undoButton, autoButton, autoEnabled);
        toolbar.setPadding(new Insets(12, 16, 0, 16));

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(new BoardView(game));
        root.setStyle("-fx-background-color: #1e6b35;");

        game.addListener(() -> {
            undoButton.setDisable(!game.canUndo());
            if (!game.isWon()) {
                winAnnounced = false;
            } else if (!winAnnounced) {
                winAnnounced = true;
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Você venceu!");
                    alert.setHeaderText(null);
                    alert.setTitle("Paciência");
                    alert.show();
                });
            }
        });
        undoButton.setDisable(!game.canUndo());

        stage.setTitle("Paciência");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
