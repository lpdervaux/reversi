package org.example.ui.reversi;

import org.example.reversi.Tile;

import java.util.Map;

/**
 * Tile maps for use with user interface.
 */
public class TileMaps {
    private TileMaps() {}

    /**
     * ASCII characters only
     */
    public static final Map<Tile, String> ASCII_TILE_MAP = Map
        .ofEntries(
            Map.entry(Tile.WHITE, "w"),
            Map.entry(Tile.BLACK, "b"),
            Map.entry(Tile.FREE, ".")
        );

    /**
     * Dot characters that should render correctly on most terminals
     */
    public static final Map<Tile, String> DOT_TILE_MAP = Map
        .ofEntries(
            Map.entry(Tile.WHITE, "o"),
            Map.entry(Tile.BLACK, "●"),
            Map.entry(Tile.FREE, "·")
        );
}
