package org.example.board.ordinal;

import org.example.board.Board;

import java.util.stream.Stream;

/**
 * Wraps a list to represent a two-dimensional board of set width and height with associated directions and coordinates.
 *
 * @param <T> Element
 */
public class OrdinalBoard<T> extends Board<T> {
    /**
     * Constructs a board {@code width} and {@code height} with elements initialized to {@code initial}.
     *
     * @param initial Initial value
     * @param width Board width, non-zero positive
     * @param height Board height, non-zero positive
     *
     * @throws IllegalArgumentException If any argument is invalid
     */
    public OrdinalBoard(T initial, int width, int height) {
        super(initial, width, height);
    }

    /**
     * Constructs an equal sided board of {@code side} with elements initialized to {@code initial}.
     *
     * @param initial Initial value
     * @param side Board side, non-zero positive
     *
     * @throws IllegalArgumentException If {@code side} is invalid
     */
    public OrdinalBoard(T initial, int side) {
        this(initial, side, side);
    }

    /**
     * Copy constructor
     *
     * @param source Source to copy
     */
    public OrdinalBoard(Board<T> source) {
        super(source);
    }

    /**
     * Validates coordinates {@code co}.
     *
     * @param co Coordinates
     * @return {@code true} if coordinates point within the board, {@code false} otherwise
     */
    public boolean validate(Coordinates co) {
        return isValid(co.x(), co.y());
    }

    /**
     * Gets element at coordinates {@code co}.
     *
     * @param co Coordinates
     * @return Element at coordinates
     *
     * @throws IndexOutOfBoundsException If coordinates do not point within the board
     */
    public T get(Coordinates co) throws IndexOutOfBoundsException {
        return get(co.x(), co.y());
    }

    /**
     * Sets element at coordinates {@code co}.
     *
     * @param co Coordinates
     * @param t Element to set
     *
     * @throws IndexOutOfBoundsException If coordinates do not point within the board
     */
    public void set(Coordinates co, T t) throws IndexOutOfBoundsException {
        set(co.x(), co.y(), t);
    }

    /**
     * @return A stream of coordinates spanning the board in row-major order
     */
    public Stream<Coordinates> traverse() {
        return Stream.iterate(
            new Coordinates(0, 0),
            c -> ( c.y() < getHeight() ),
            c -> ( c.x() == getWidth() - 1 )
                ? new Coordinates(0, c.y() + 1)
                : new Coordinates(c.x() + 1, c.y())
        );
    }
}
