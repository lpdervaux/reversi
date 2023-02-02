package org.example.ui.reversi;

import org.example.reversi.Tile;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Builds a state {@code String} from a {@code Game}. Immutable.
 *
 * Favors {@code String.format} for readability.
 */
class IndexedGameGridBuilder {
    private final StringBuilder builder;

    private final Map<Tile, String> tileMap;
    private final org.example.reversi.Game game;

    /**
     * Builds an indexed grid of {@code game} using {@code tileMap}.
     * // TODO: move turn state elsewhere
     *
     * @param tileMap Tile map to use
     * @param game
     */
    public IndexedGameGridBuilder(Map<Tile, String> tileMap, org.example.reversi.Game game) {
        this.builder = new StringBuilder();
        this.tileMap = tileMap;
        this.game = game;

        build();
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    private void build() {
        appendBoard();
    }

    private void appendBoard() {
        appendBoardHeader();
        appendBoardRows();
    }

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

    private String headerIndex() {
        return IntStream.range(0, game.width())
            .map(x -> x % 10)
            .mapToObj(x -> String.format("%d ", x))
            .collect(Collectors.joining());
    }

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

    private String boardRow(int y) {
        return game.row(y)
            .map(tileMap::get)
            .map(s -> String.format("%s ", s))
            .collect(Collectors.joining());
    }
}
