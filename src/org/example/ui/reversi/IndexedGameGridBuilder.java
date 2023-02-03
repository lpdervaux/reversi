package org.example.ui.reversi;

import org.example.reversi.Tile;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Builds a state {@code String} from a {@code Game}. Immutable.
 */
// TODO: class to be removed
class IndexedGameGridBuilder {
    private final StringBuilder builder;

    private final Map<Tile, String> tileMap;
    private final org.example.reversi.Game game;

    /**
     * Builds an indexed grid of {@code game} using {@code tileMap}.
     *
     * @param tileMap Tile map to use
     * @param game Game to build for
     */
    public IndexedGameGridBuilder(Map<Tile, String> tileMap, org.example.reversi.Game game) {
        this.builder = new StringBuilder();
        this.tileMap = tileMap;
        this.game = game;

        build();
    }

    /**
     * @return Indexed grid string
     */
    @Override
    public String toString() {
        return builder.toString();
    }

    /**
     * Builds indexed grid.
     */
    private void build() {
        appendBoard();
    }

    /**
     * Appends board representation to {@code builder}.
     */
    private void appendBoard() {
        appendBoardHeader();
        appendBoardRows();
    }

    /**
     * Appends board header to {@code builder}.
     */
    private void appendBoardHeader() {
        builder.append(
            String.format(
                """
                  %s
                """,
                headerIndex()
            )
        );
    }

    /**
     * @return Header index of {@code game.width()}
     */
    private String headerIndex() {
        return IntStream.range(0, game.width())
            .map(x -> x % 10)
            .mapToObj(x -> String.format("%d ", x))
            .collect(Collectors.joining());
    }

    /**
     * Appends board row grid to {@code builder}.
     */
    private void appendBoardRows() {
        IntStream.range(0, game.height())
            .forEachOrdered(
                y -> {
                    builder.append(
                        String.format(
                            """
                            %d %s
                            """,
                            y % 10,
                            boardRow(y)
                        )
                    );
                }
        );
    }

    /**
     * @param y Row index
     * @return Row as a space separated string of symbols mapped by {@code tileMap}
     */
    private String boardRow(int y) {
        return game.row(y)
            .map(tileMap::get)
            .map(s -> String.format("%s ", s))
            .collect(Collectors.joining());
    }
}
