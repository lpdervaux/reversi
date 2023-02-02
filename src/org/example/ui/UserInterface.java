package org.example.ui;

import java.io.IOError;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Extends {@code ScannerUserInterface} with generic user interface methods.
 */
public abstract class UserInterface extends ScannerUserInterface {
    protected UserInterface() {}

    /**
     * Prompts for a menu choice until match using static {@code Enum} based menu.
     * Presents {@code prompt} followed by menu until a matching {@code Menu.choice()} is input.
     *
     * @param prompt Prompt to present
     * @param menu Enumeration class
     * @return Matching enumeration member
     *
     * @param <T> An {@code Enum} implementing {@code Menu}
     */
    protected <T extends Enum<T> & Menu> T promptUntilMenuChoice(String prompt, Class<T> menu) {
        var set = EnumSet.allOf(menu);

        var promptAndMenu = prompt +
            set.stream().map(
                m -> String.format("[%s] %s%n", m.choice(), m.description())
            )
            .collect(Collectors.joining());

        return promptUntil(promptAndMenu, s -> menuParser(s, set));
    }

    /**
     * Returns enumeration member corresponding to {@code input}.
     * Composed method of {@code promptUntilMenuChoice}.
     *
     * @param input String to match against enumeration
     * @param set Set of enumeration
     * @return Matching enumeration member
     *
     * @param <T> An {@code Enum} implementing {@code Menu}
     *
     * @throws IllegalArgumentException If no matching {@code Menu.choice()} exists
     */
    static private <T extends Enum<T> & Menu> T menuParser(String input, EnumSet<T> set) {
        return set.stream()
            .filter(m -> m.choice().equals(input))
            .findAny()
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format("Unknown selection: %s", input)
                )
            );
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
