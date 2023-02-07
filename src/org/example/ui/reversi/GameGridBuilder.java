package org.example.ui.reversi;

import org.example.reversi.Game;
import org.example.reversi.Tile;

import java.util.Map;
import java.util.stream.IntStream;

/**
 * Component of {@code UserInterface}.
 * StringBuilder implementation for creating the indexed game grid.
 */
/*
    The previous simple implementation using String.format with Collectors.joining was unacceptably slow as it would overflow the JVM String pool for large grids.
    Note that even using a more efficient grid builder, large console IO itself seems to be a significant bottleneck.
 */
class GameGridBuilder {
    static private final int SPACE = " ".codePointAt(0);

    private final StringBuilder builder;
    private final Game game;
    private final Map<Tile, String> tileMap;

    /**
     * Builds an indexed grid for {@code game} using {@code tileMap}.
     *
     * @param game Source game
     * @param tileMap Source tile map
     */
    public GameGridBuilder(Game game, Map<Tile, String> tileMap) {
        this.game = game;
        this.tileMap = tileMap;

        // in case of double char unicode tile map, this guess for initial capacity would require a single buffer extension
        var line_capacity = game.getWidth() * 2 + 6; // deliberate extra buffer
        var line_count = game.getHeight() + 2; // deliberate extra buffer

        this.builder = new StringBuilder(line_capacity * line_count);

        build();
    }

    /**
     * @return Indexed grid
     */
    @Override
    public String toString() {
        return builder.toString();
    }

    // self-explanatory
    private void build() {
        appendHeader();
        appendRows();
    }

    /**
     * Appends header as (empty y column index) + (indexes) + (line separator).
     */
    private void appendHeader() {
        builder.appendCodePoint(SPACE).appendCodePoint(SPACE);
        IntStream.range(0, game.getWidth())
            .forEachOrdered(this::appendIndex);
        builder.append(System.lineSeparator());
    }

    // self-explanatory
    private void appendRows() {
        IntStream.range(0, game.getHeight())
            .forEachOrdered(this::appendRow);
    }

    /**
     * Appends a row as (y column index) + (tiles) + (line separator).
     *
     * @param y Row index
     */
    private void appendRow(int y) {
        appendIndex(y);
        game.getRow(y)
            .forEachOrdered(this::appendTile);
        builder.append(System.lineSeparator());
    }

    /**
     * Appends an index value as a modulo of 10.
     *
     * @param i Index
     */
    private void appendIndex(int i) {
        builder.append(i % 10).appendCodePoint(SPACE);
    }

    /**
     * Appends a mapped tile.
     *
     * @param t Tile
     */
    private void appendTile(Tile t) {
        builder.append(tileMap.get(t)).appendCodePoint(SPACE);
    }
}
