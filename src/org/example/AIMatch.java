package org.example;

import org.example.reversi.Game;
import org.example.reversi.ai.RandomAI;

/**
 * Performs a timed RandomAI match on a static size board.
 */
public class AIMatch {
    private static final int SIZE = 128;

    /**
     * Times a runnable.
     *
     * @param runnable Function to time
     * @return Time spent within function
     */
    private static long timeRunnable(Runnable runnable) {
        var startTime = System.nanoTime();
        runnable.run();
        var endTime = System.nanoTime();

        return endTime - startTime;
    }

    /**
     * Has RandomAI play a game to completion.
     *
     * @param game Game to play
     */
    private static void runAIGame(Game game) {
        do {
            game.nextMove(RandomAI.nextMove(game));
        } while ( !game.isOver() );
    }

    public static void main(String[] args) {
        System.out.printf("Running AI match on %d x %d = %d tiles board%n", SIZE, SIZE, SIZE * SIZE);

        var game = new Game(SIZE, SIZE);
        var elapsed = timeRunnable(() -> runAIGame(game));

        System.out.printf("Running time: %,d ns%n", elapsed);
        System.out.printf(
            """
            
            Final result:
            Turn: %d
            Score: %s %d %s %d
            """,
            game.getTurn(),
            game.getWhite().getColor(), game.getWhite().getScore(), game.getBlack().getColor(), game.getBlack().getScore()
        );
    }
}
