package org.example.board.ordinal;

import org.example.board.Board;

import java.util.List;
import java.util.stream.Stream;

/**
 * Wraps a list to represent a two-dimensional board of set width and height with associated directions and coordinates.
 *
 * @param <T> Element
 */
public class OrdinalBoard<T> extends Board<T> {
    /**
     * Construct a board of {@code width} and {@code height} from {@code list}.
     *
     * @param list List of exactly {@code width * height} elements
     * @param width Board width, non-zero positive
     * @param height Board height, non-zero positive
     *
     * @throws IllegalArgumentException If any argument is invalid
     */
    public OrdinalBoard(List<T> list, int width, int height) {
        super(list, width, height);
    }

    /**
     * Construct an equal sided board of {@code side} from {@code list}.
     *
     * @param list List of exactly {@code side * side} elements
     * @param side Board side, non-zero positive
     *
     * @throws IllegalArgumentException If any argument is invalid
     */
    public OrdinalBoard(List<T> list, int side) {
        super(list, side);
    }

    /**
     * Copy constructor.
     *
     * @param board Source to copy
     */
    public OrdinalBoard(OrdinalBoard<T> board) {
        super(board);
    }

    /**
     * Validates coordinates {@code co}.
     *
     * @param co Coordinates
     * @return {@code true} if coordinates point within the board, {@code false} otherwise
     */
    public boolean validate(Coordinates co) {
        return validate(co.x(), co.y());
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
     * @return A stream of coordinates spanning the board in increasing row order
     */
    public Stream<Coordinates> traverse() {
        return Stream.iterate(
            new Coordinates(0, 0),
            c -> ( c.y() < height() ),
            c -> ( c.x() == width() - 1 )
                ? new Coordinates(0, c.y() + 1)
                : new Coordinates(c.x() + 1, c.y())
        );
    }

    /**
     * Get a stream of coordinates from {@code origin} to an edge of the board in {@code direction}.
     *
     * @param origin Starting coordinates
     * @param direction Direction to traverse
     * @return A stream of coordinates from origin inclusive to the edge of the board, which may be empty if origin is not within the board
     */
    public Stream<Coordinates> direction(Coordinates origin, Direction direction) {
        return Stream.iterate(origin, this::validate, direction.next());
    }
}
