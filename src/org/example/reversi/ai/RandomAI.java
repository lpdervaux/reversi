package org.example.reversi.ai;

import org.example.board.ordinal.Coordinates;
import org.example.reversi.Game;

import java.util.concurrent.ThreadLocalRandom;

/**
 * AI that selects a random valid move.
 */
public class RandomAI {
    // non-instantiable
    private RandomAI() {}

    static private final int LIMIT = 10;

    /**
     * Selects a random valid move for a game. Searches for up to 10 moves.
     *
     * @param game Game to compute next move for
     * @return Random valid move
     *
     * @throws IllegalArgumentException If game is over
     */
    static public Coordinates nextMove(Game game) {
        if ( game.over() ) throw new IllegalArgumentException();

        var moves = game.findValidMoves()
            .limit(LIMIT)
            .toList();

        return moves.get(
                ThreadLocalRandom.current().nextInt(moves.size())
            );
    }
}
