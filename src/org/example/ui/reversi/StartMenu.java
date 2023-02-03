package org.example.ui.reversi;

import org.example.ui.Menu;

/**
 * Menu presenting game settings selection and start option.
 *
 * @see Menu
 */
public enum StartMenu implements Menu {
    START("s", "Start"),
    SIZE("b","Board size"),
    PLAYER("p","Players"),
    TILE("t", "Tile map");

    private final String choice;
    private final String description;

    StartMenu(String choice, String description) {
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
