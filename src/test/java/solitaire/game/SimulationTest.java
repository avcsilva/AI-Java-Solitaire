package solitaire.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static solitaire.game.GameFixtures.snapshot;

import java.util.Random;
import org.junit.jupiter.api.Test;
import solitaire.strategy.PriorityStrategy;

/** Automatic games over many seeds: termination (RF30), full undo (RF25) and score statistics (RF03). */
class SimulationTest {

    private static final int GAMES = 500;
    private static final int MAX_PLAYS = 1000;

    @Test
    void automaticPlayTerminatesAndUndoAllRestoresInitialState() {
        int wins = 0;
        long totalScore = 0;
        for (int seed = 0; seed < GAMES; seed++) {
            Game game = new Game(new Random(seed), new PriorityStrategy());
            String initial = snapshot(game);

            int plays = playUntilStuck(game);
            assertTrue(plays < MAX_PLAYS, "Automatic play did not terminate for seed " + seed);
            totalScore += game.score();
            if (game.isWon()) {
                wins++;
            }
            String finalState = snapshot(game);

            int undone = 0;
            while (game.undo()) {
                undone++;
            }
            assertEquals(plays, undone);
            assertEquals(initial, snapshot(game), "Undo all did not restore seed " + seed);
            assertFalse(game.canUndo());

            assertEquals(plays, playUntilStuck(game));
            assertEquals(finalState, snapshot(game));
        }
        System.out.printf("Simulated %d games: %d wins, average score %.2f%n",
                GAMES, wins, (double) totalScore / GAMES);
    }

    private static int playUntilStuck(Game game) {
        int plays = 0;
        while (plays < MAX_PLAYS && game.autoPlay()) {
            plays++;
        }
        return plays;
    }
}
