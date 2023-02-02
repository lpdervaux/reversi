package org.example.reversi;

import org.example.board.ordinal.OrdinalBoard;

import java.util.Arrays;
import java.util.List;

// TODO: maybe get rid of this class, it has no purpose
class Board extends OrdinalBoard<Tile> {
    static protected Board create(int width, int height) {
        if (
            width % 2 != 0 || height % 2 != 0
                || width < 4 || height < 4
        ) throw new IllegalArgumentException();

        return new Board(Arrays.asList(new Tile[width * height]), width, height);
    }

    // Since super() call must be the first statement, we cannot put restrictions on initialization parameters within constructor.
    // Without implementing a default constructor in the parent class
    private Board(List<Tile> board, int width, int height) {
        super(board, width, height);
    }
}
