package org.example.ui.reversi;

import org.example.reversi.Tile;
import org.example.ui.UserInterfaceSubordinate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tile map menu component of {@code StartMenu}.
 */
class TileMapMenu extends UserInterfaceSubordinate {
    static private final Map<Tile, String> DEFAULT_TILE_MAP = TileMaps.DOT_TILE_MAP;

    static private final Map<String, String> TILE_MAP_MENU;

    static {
        TILE_MAP_MENU = new LinkedHashMap<>(2, 1.0f);
        TILE_MAP_MENU.put("a", "ASCII: w b .");
        TILE_MAP_MENU.put("d", "Dot: o ● ·");
    }

    private Map<Tile, String> tileMap;

    protected TileMapMenu(UserInterface main) {
        super(main);
        this.tileMap = DEFAULT_TILE_MAP;
    }

    /**
     * @return Tile map
     */
    public Map<Tile, String> getTileMap() {
        return tileMap;
    }

    /**
     * Prompts user for tile map setting.
     */
    public void prompt() {
        var tile = promptUntilMenuChoice("Please select a tile map." + System.lineSeparator(), TILE_MAP_MENU);

        switch (tile) {
            case "a" -> tileMap = TileMaps.ASCII_TILE_MAP;
            case "d" -> tileMap = TileMaps.DOT_TILE_MAP;
        }
    }
}
