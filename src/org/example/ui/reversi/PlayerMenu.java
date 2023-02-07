package org.example.ui.reversi;

import org.example.ui.UserInterfaceSubordinate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Player menu component of {@code StartMenu}.
 */
class PlayerMenu extends UserInterfaceSubordinate {
    static private final boolean DEFAULT_AI_WHITE = true;
    static private final boolean DEFAULT_AI_BLACK = true;

    static private final Map<String, String> PLAYER_MENU;

    static {
        PLAYER_MENU = new LinkedHashMap<>(2, 1.0f);
        PLAYER_MENU.put("h", "Human");
        PLAYER_MENU.put("a", "AI");
    }

    private boolean whiteAIActive;
    private boolean blackAIActive;

    public PlayerMenu(UserInterface main) {
        super(main);
        whiteAIActive = DEFAULT_AI_WHITE;
        blackAIActive = DEFAULT_AI_BLACK;
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
     * Prompts for a player AI setting.
     *
     * @param prompt Prompt to display
     * @return {@code true} for AI controlled, {@code false} otherwise
     */
    private boolean promptPlayerAI(String prompt) {
        var choice = promptUntilMenuChoice(prompt, PLAYER_MENU);
        return ( choice.equals("a") );
    }
}
