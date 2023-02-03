package org.example.ui.reversi;

import org.example.ui.Menu;

/**
 * Menu for tile map setting presenting the choices of ASCII and dot.
 *
 * @see Menu
 */
public enum TileMapMenu implements Menu {
    ASCII("a", "ASCII: w b ."),
    DOT("d", "Dot: o ● ·");

    private final String choice;
    private final String description;

    TileMapMenu(String choice, String description) {
        this.choice = choice;
        this.description = description;
    }

    @Override
    public String choice() {
        return choice;
    }

    @Override
    public String description() {
        return description;
    }
}
