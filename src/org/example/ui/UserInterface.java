package org.example.ui;

import java.io.IOError;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Extends {@code ScannerUserInterface} with generic user interface methods.
 */
public abstract class UserInterface extends ScannerUserInterface {
    protected UserInterface() {}

    /**
     * Prompts for a menu choice until match.
     *
     * @param prompt Prompt to present
     * @param menu Enumeration class
     * @return Matching enumeration member
     */
    protected String promptUntilMenuChoice(String prompt, Map<String, String> menu) {
        var menuPrompt = new StringBuilder();

        menuPrompt.append(prompt);
        menu.entrySet().stream()
            .map(
                e -> String.format("[%s] %s%n", e.getKey(), e.getValue())
            )
            .forEachOrdered(menuPrompt::append);

        return promptUntil(menuPrompt.toString(), s -> menuParser(s, menu));
    }

    /**
     * Parses input for menu key.
     * Throws a descriptive {@code IllegalArgumentException} for use with {@code promptUntil} if parsing fails.
     * <p>
     * Composed method of {@code promptUntilMenuChoice}.
     *
     * @param input Input to parse
     * @param menu Menu map
     * @return Menu key equal to input
     *
     * @throws IllegalArgumentException If input is not within key set
     *
     * @see #promptUntilMenuChoice(String, Map)
     */
    private String menuParser(String input, Map<String, String> menu) throws IllegalArgumentException {
        if ( !menu.containsKey(input) )
            throw new IllegalArgumentException(
                String.format("Unknown selection: %s", input)
            );

        return input;
    }

    /**
     * Presents {@code prompt} until {@code parser} successfully parses a line read from {@code stdin}.
     * Displays {@code IllegalArgumentException.getMessage()} as they occur.
     *
     * @param prompt Prompt to present
     * @param parser Parser to operate on input line
     * @return Parser's resulting value
     */
    protected <T> T promptUntil(String prompt, InputParser<T> parser) {
        T t;

        for (;;) {
            System.out.print(prompt);

            String s = nextLine();
            try {
                t = parser.parse(s);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        return t;
    }

    /**
     * Scans next line from {@code stdin}.
     *
     * @return Next line
     *
     * @throws IOError If reading from {@code System.in} fails
     */
    protected String nextLine() {
        String s;
        try {
            s = stdin.nextLine();
        } catch (NoSuchElementException e) {
            throw new IOError(e); // System.in is closed or otherwise unusable
        }
        return s;
    }
}
