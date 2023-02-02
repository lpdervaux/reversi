package org.example.reversi.ai;

import org.example.board.ordinal.Coordinates;
import org.example.reversi.Game;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Dummy AI that selects a random valid move.
 */
public class RandomAI {
    private RandomAI() {}

    static public Coordinates nextMove(Game game) {
        if ( game.over() ) throw new IllegalArgumentException();

        var moves = game.validMoves();
        return moves.get(
            ThreadLocalRandom.current().nextInt(moves.size())
        );
    }
}
