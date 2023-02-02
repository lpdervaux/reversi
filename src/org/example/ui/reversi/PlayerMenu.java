package org.example.ui.reversi;

import org.example.ui.Menu;

public enum PlayerMenu implements Menu {
    HUMAN("h", "Human"),
    AI("a", "AI");

    private final String choice;
    private final String description;

    PlayerMenu(String choice, String description) {
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
