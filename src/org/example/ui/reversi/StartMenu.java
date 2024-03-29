package org.example.ui.reversi;

import org.example.ui.UserInterfaceSubordinate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Start menu component of {@code UserInterface}.
 */
class StartMenu extends UserInterfaceSubordinate {
    static private final Map<String, String> START_MENU;

    static {
        START_MENU = new LinkedHashMap<>(5, 1.0f);
        START_MENU.put("b", "Board size");
        START_MENU.put("p", "Players");
        START_MENU.put("t", "Tile map");
        START_MENU.put("d", "Display");
        START_MENU.put("s", "Start");
    }

    private final SizeMenu sizeMenu;
    private final PlayerMenu playerMenu;
    private final TileMapMenu tileMapMenu;
    private final DisplayMenu displayMenu;

    public StartMenu(UserInterface main) {
        super(main);
        this.sizeMenu = new SizeMenu(main);
        this.playerMenu = new PlayerMenu(main);
        this.tileMapMenu = new TileMapMenu(main);
        this.displayMenu = new DisplayMenu(main);
    }

    /**
     * @return Size menu
     */
    public SizeMenu getSizeMenu() {
        return sizeMenu;
    }

    /**
     * @return Player menu
     */
    public PlayerMenu getPlayerMenu() {
        return playerMenu;
    }

    /**
     * @return Tile map menu
     */
    public TileMapMenu getTileMapMenu() {
        return tileMapMenu;
    }

    /**
     * @return Display menu
     */
    public DisplayMenu getDisplayMenu() {
        return displayMenu;
    }

    /**
     * Prompts user for settings until start is chosen.
     */
    public void promptUntilStart() {
        String choice;

        do {
            choice = promptStartMenu();
            switch (choice) {
                case "b" -> sizeMenu.prompt();
                case "p" -> playerMenu.prompt();
                case "t" -> tileMapMenu.prompt();
                case "d" -> displayMenu.prompt();
            }
        } while ( !choice.equals("s") );
    }

    /**
     * Prompts user for menu until a valid choice is input.
     *
     * @return Valid menu choice
     */
    private String promptStartMenu() {
        return promptUntilMenuChoice(
            String.format(
                """
                    [Options]
                    Board: %d x %d
                    Players: White (%s) Black (%s)
                    Tiles: %s
                    Display: Grid %s Turn %s
                    """,
                sizeMenu.getWidth(), sizeMenu.getHeight(),
                playerMenu.isWhiteAIActive() ? "AI" : "Human", playerMenu.isBlackAIActive() ? "AI" : "Human",
                tileMapMenu.getTileMap().values().stream()
                    .map(s -> String.format("%s ", s))
                    .collect(Collectors.joining()),
                displayMenu.isGridDisplay() ? "ON" : "OFF", displayMenu.isTurnDisplay() ? "ON" : "OFF"
            ),
            START_MENU
        );
    }
}
