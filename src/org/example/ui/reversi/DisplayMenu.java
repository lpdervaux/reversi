package org.example.ui.reversi;

import org.example.ui.UserInterfaceSubordinate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Display menu component of {@code StartMenu}.
 */
public class DisplayMenu extends UserInterfaceSubordinate {
    static private final boolean DEFAULT_GRID_DISPLAY = true;
    static private final boolean DEFAULT_TURN_DISPLAY = true;

    static private final Map<String, String> DISPLAY_MENU;

    static {
        DISPLAY_MENU = new LinkedHashMap<>(2, 1.0f);
        DISPLAY_MENU.put("g", "Grid and turn information");
        DISPLAY_MENU.put("t", "Turn information only");
        DISPLAY_MENU.put("n", "Nothing");
    }

    private boolean gridDisplay;
    private boolean turnDisplay;

    public DisplayMenu(UserInterface main) {
        super(main);
        this.gridDisplay = DEFAULT_GRID_DISPLAY;
        this.turnDisplay = DEFAULT_TURN_DISPLAY;
    }

    /**
     * @return {@code true} if grid display is active, {@code false} otherwise
     */
    public boolean isGridDisplay() {
        return gridDisplay;
    }

    /**
     * @return {@code true} if turn display is active, {@code false} otherwise
     */
    public boolean isTurnDisplay() {
        return turnDisplay;
    }

    /**
     * Prompts user for display settings.
     */
    public void prompt() {
        var display = promptUntilMenuChoice(
            "Please select a display option." + System.lineSeparator(),
            DISPLAY_MENU
        );

        switch (display) {
            case "g" -> { gridDisplay = true; turnDisplay = true; }
            case "t" -> { gridDisplay = false; turnDisplay = true; }
            case "n" -> { gridDisplay = false; turnDisplay = false; }
        }
    }
}
