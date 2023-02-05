package org.example.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Wraps a list to represent a two-dimensional board of set width and height.
 *
 * @param <E> Element
 */
public class Board<E> {
    private final List<E> board;
    private final int width;
    private final int height;

    /**
     * Constructs a board {@code width} and {@code height} with elements initialized to {@code initial}.
     *
     * @param initial Initial value
     * @param width Board width, non-zero positive
     * @param height Board height, non-zero positive
     *
     * @throws IllegalArgumentException If any argument is invalid
     */
    public Board(E initial, int width, int height) throws IllegalArgumentException {
        if ( width <= 0 || height <= 0 )
            throw new IllegalArgumentException();

        var size = width * height;
        this.board = new ArrayList<>(Collections.nCopies(size, initial));
        this.width = width;
        this.height = height;
    }

    /**
     * Constructs an equal sided board of {@code side} with elements initialized to {@code initial}.
     *
     * @param initial Initial value
     * @param side Board side, non-zero positive
     *
     * @throws IllegalArgumentException If {@code side} is invalid
     */
    public Board(E initial, int side) throws IllegalArgumentException {
        this(initial, side, side);
    }

    /**
     * Copy constructor
     *
     * @param source Source to copy
     */
    public Board(Board<E> source) {
        this.board = new ArrayList<>(source.board);
        this.width = source.width;
        this.height = source.height;
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
    public E get(int x, int y) throws IndexOutOfBoundsException {
        if ( !validate(x, y) ) throw new IndexOutOfBoundsException();
        return board.get(linearIndex(x, y));
    }

    /**
     * Sets element at (x y)
     *
     * @param x Horizontal axis coordinate
     * @param y Vertical axis coordinate
     * @param e Element to set
     *
     * @throws IndexOutOfBoundsException If (x y) does not point within the board
     */
    public void set(int x, int y, E e) {
        if ( !validate(x, y) ) throw new IndexOutOfBoundsException();
        board.set(linearIndex(x, y), e);
    }

    /**
     * @return Ordered stream of all elements in row-major order
     */
    public Stream<E> stream() {
        return board.stream();
    }

    /**
     * @param y Row index
     * @return Ordered stream of row {@code y} in column order
     *
     * @throws IndexOutOfBoundsException If index is not within the board
     */
    public Stream<E> row(int y) throws IndexOutOfBoundsException {
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
    public Stream<E> column(int x) throws IndexOutOfBoundsException {
        if ( x < 0 || x >= width ) throw new IndexOutOfBoundsException();
        return IntStream.range(0, height)
            .mapToObj(y -> get(x, y));
    }
}
