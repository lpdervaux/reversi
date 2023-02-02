package org.example.ui;

/**
 * Defines a parser of {@code String} into result.
 * Meant for use with {@code UserInterface.promptUntil}, which uses this interface to implement simple prompts.
 */
public interface InputParser<T> {
    /**
     * Parses a {@code String} into a result.
     *
     * @param s Input string
     * @return Parsed value
     * @throws IllegalArgumentException If input is invalid; contains a descriptive, printable error message
     */
    T parse(String s) throws IllegalArgumentException;
}
