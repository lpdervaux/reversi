package org.example.board;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Wraps a list to represent a two-dimensional board of set width and height.
 *
 * @param <T> Element
 */
public class Board<T> {
    private final List<T> board;
    private final int width;
    private final int height;

    /**
     * Construct a board of {@code width} and {@code height} from {@code list}.
     *
     * @param list List of exactly {@code width * height} elements
     * @param width Board width, non-zero positive
     * @param height Board height, non-zero positive
     *
     * @throws IllegalArgumentException If any argument is invalid
     */
    public Board(List<T> list, int width, int height) throws IllegalArgumentException {
        if (
            width <= 0
            || height <= 0
            || list.size() != width * height
        ) throw new IllegalArgumentException();
        this.board = new ArrayList<>(list);
        this.width = width;
        this.height = height;
    }

    /**
     * Construct an equal sided board of {@code side} from {@code list}.
     *
     * @param list List of exactly {@code side * side} elements
     * @param side Board side, non-zero positive
     *
     * @throws IllegalArgumentException If any argument is invalid
     */
    public Board(List<T> list, int side) throws IllegalArgumentException {
        this(list, side, side);
    }

    /**
     * Copy constructor.
     *
     * @param board Source to copy
     */
    public Board(Board<T> board) {
        this(board.board, board.width, board.height);
    }

    /**
     * Maps a pair of coordinates to internal list index
     *
     * @param x Horizontal axis coordinate
     * @param y Vertical axis coordinate
     * @return Corresponding index in the internal list
     */
    private int linearIndex(int x, int y) {
        return y * width + x;
    }

    /**
     * @return Width of this board
     */
    public int width() {
        return width;
    }

    /**
     * @return Height of this board
     */
    public int height() {
        return height;
    }

    /**
     * Validates coordinates (x y).
     *
     * @param x Horizontal axis coordinate
     * @param y Vertical axis coordinate
     * @return {@code true} if (x y) points within the board, {@code false} otherwise
     */
    public boolean validate(int x, int y) {
        return (
            x >= 0 && x < width
            && y >= 0 && y < height
        );
    }

    /**
     * Retrieves element at (x y).
     *
     * @param x Horizontal axis coordinate
     * @param y Vertical axis coordinate
     * @return Element at (x y)
     *
     * @throws IndexOutOfBoundsException If (x y) does not point within the board
     */
    public T get(int x, int y) throws IndexOutOfBoundsException {
        if ( !validate(x, y) ) throw new IndexOutOfBoundsException();
        return board.get(linearIndex(x, y));
    }

    /**
     * Sets element at (x y)
     *
     * @param x Horizontal axis coordinate
     * @param y Vertical axis coordinate
     * @param t Element to set
     *
     * @throws IndexOutOfBoundsException If (x y) does not point within the board
     */
    public void set(int x, int y, T t) {
        if ( !validate(x, y) ) throw new IndexOutOfBoundsException();
        board.set(linearIndex(x, y), t);
    }

    /**
     * @return Ordered stream of all elements in row-major order
     */
    public Stream<T> stream() {
        return board.stream();
    }

    /**
     * @param y Row index
     * @return Ordered stream of row {@code y} in column order
     *
     * @throws IndexOutOfBoundsException If index is not within the board
     */
    public Stream<T> row(int y) throws IndexOutOfBoundsException {
        if ( y < 0 || y >= height ) throw new IndexOutOfBoundsException();
        return IntStream.range(0, width)
            .mapToObj(x -> get(x, y));
    }

    /**
     * @param x Column index
     * @return Ordered stream of column {@code x} in row order
     *
     * @throws IndexOutOfBoundsException If index is not within the board
     */
    public Stream<T> column(int x) throws IndexOutOfBoundsException {
        if ( x < 0 || x >= width ) throw new IndexOutOfBoundsException();
        return IntStream.range(0, height)
            .mapToObj(y -> get(x, y));
    }
}
