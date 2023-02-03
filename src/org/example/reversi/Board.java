package org.example.reversi;

import org.example.board.ordinal.OrdinalBoard;

import java.util.Arrays;
import java.util.List;

/**
 * An {@code OrdinalBoard<Tile>} with restricted size.
 * TODO: remove this class and move validation to Game
 */
class Board extends OrdinalBoard<Tile> {
    /**
     * Validates width and height, then creates a new {@code Board} instance from a new backing {@code Tile[]}.
     *
     * @param width Multiple of 2 and greater than or equal to 4
     * @param height Multiple of 2 and greater than or equal to 4
     * @return New {@code Board instance}
     *
     * @throws IllegalArgumentException If either size isn't a multiple of 2 or is less than 4
     */
    static protected Board create(int width, int height) {
        if (
            width % 2 != 0 || height % 2 != 0
                || width < 4 || height < 4
        ) throw new IllegalArgumentException();

        return new Board(Arrays.asList(new Tile[width * height]), width, height);
    }

    // only instantiable through create()
    private Board(List<Tile> board, int width, int height) {
        super(board, width, height);
    }
}
