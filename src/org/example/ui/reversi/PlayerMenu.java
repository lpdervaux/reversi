package org.example.ui.reversi;

import org.example.ui.UserInterfaceSubordinate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Component of {@code UserInterface}.
 */
class PlayerMenu extends UserInterfaceSubordinate {
    static private final Map<String, String> PLAYER_MENU;

    static {
        PLAYER_MENU = new LinkedHashMap<>(2, 1.0f); // LinkedHashMap to preserve iteration order
        PLAYER_MENU.put("h", "Human");
        PLAYER_MENU.put("a", "AI");
    }

    private boolean whiteAIActive;
    private boolean blackAIActive;

    public PlayerMenu(UserInterface main) {
        super(main);
        whiteAIActive = false;
        blackAIActive = true;
    }

    /**
     * @return {@code true} if white AI is set active, {@code false} otherwise
     */
    public boolean isWhiteAIActive() {
        return whiteAIActive;
    }

    /**
     * @return {@code true} if black AI is set active, {@code false} otherwise
     */
    public boolean isBlackAIActive() {
        return blackAIActive;
    }

    /**
     * Prompts user to set white and black AI settings.
     */
    public void prompt() {
        whiteAIActive = promptPlayerAI("Configure white" + System.lineSeparator());
        blackAIActive = promptPlayerAI("Configure black" + System.lineSeparator());
    }

    /**
     * Prompts for a single player setting.
     *
     * @param prompt Prompt to display
     * @return {@code true} for AI controlled, {@code false} otherwise
     */
    private boolean promptPlayerAI(String prompt) {
        var choice = promptUntilMenuChoice(prompt, PLAYER_MENU);
        return ( choice.equals("a") );
    }
}
