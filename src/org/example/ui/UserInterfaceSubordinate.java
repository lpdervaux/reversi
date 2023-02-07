package org.example.ui;

import org.example.ui.reversi.UserInterface;

import java.io.IOError;
import java.util.Map;

/**
 * Component of a {@code UserInterface}.
 */
public abstract class UserInterfaceSubordinate {
    private final UserInterface main;

    /**
     * Binds to a {@code UserInterface}.
     *
     * @param main {@code UserInterface} to bind to
     */
    protected UserInterfaceSubordinate(UserInterface main) {
        this.main = main;
    }

    /**
     * Calls main {@code UserInterface}'s {@code promptUntilMenuChoice}.
     *
     * @param prompt Prompt to present
     * @param menu Map of choices pointing to descriptions
     * @return Selected choice
     */
    protected String promptUntilMenuChoice(String prompt, Map<String, String> menu) {
        return main.promptUntilMenuChoice(prompt, menu);
    }

    /**
     * Calls main {@code UserInterface}'s {@code promptUntil}.
     *
     * @param prompt Prompt to present
     * @param parser Parser to operate on input line
     * @return Parser's resulting value
     */
    protected <T> T promptUntil(String prompt, InputParser<T> parser) {
        return main.promptUntil(prompt, parser);
    }

    /**
     * Calls main {@code UserInterface}'s {@code nextLine}.
     *
     * @return Next line
     *
     * @throws IOError If reading from {@code System.in} fails
     */
    protected String nextLine() {
        return main.nextLine();
    }
}
