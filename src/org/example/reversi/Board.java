package org.example.reversi;

import org.example.board.ordinal.Coordinates;
import org.example.board.ordinal.Direction;
import org.example.board.ordinal.OrdinalBoard;

import java.util.*;
import java.util.stream.Stream;


/**
 * Represents a Reversi board.
 */
public class Board {
    private final OrdinalBoard<Tile> board;
    private final Set<Coordinates> edges;

    /**
     * Constructs a board with initial state.
     *
     * @param initial Initial state
     */
    public Board(org.example.board.Board<Tile> initial) {
        this.board = new OrdinalBoard<>(initial);

        var capacity = board.width() * board.height() / 2;
        this.edges = new HashSet<>(capacity);
        initializeEdges();
    }

    /**
     * Copy constructor.
     *
     * @param source Source to copy
     */
    public Board(Board source) {
        this.board = new OrdinalBoard<>(source.board);
        this.edges = new HashSet<>(source.edges);
    }

    /**
     * Performs {@code move} for {@code color}.
     *
     * @param color Capturing color
     * @param move Coordinates of move
     * @return Number of tiles captured, which is at least one
     *
     * @throws IllegalArgumentException If move is invalid
     */
    public int move(Color color, Coordinates move) throws IllegalArgumentException {
        if ( !isValidMove(color, move) ) throw new IllegalArgumentException();

        board.set(move, color.tile());
        updateEdges(move);

        return encloseAll(color, move)
            .reduce(
                0,
                (a, c) -> {
                    board.set(c, color.tile());
                    return a + 1;
                },
                Integer::sum
            );
    }

    /**
     * Determines if {@code move} by {@code color} is valid.
     * <p>
     * A valid move is one that targets a free tile and creates one or more enclosing group.
     *
     * @param color Capturing color
     * @param move Move to check
     * @return {@code true} if valid, {@code false} otherwise
     */
    public boolean isValidMove(Color color, Coordinates move) {
        boolean valid;

        if ( board.get(move) != Tile.FREE )
            valid = false;
        else
            valid = encloseAny(color, move);

        return valid;
    }

    /**
     * Finds any valid move for {@code color}.
     * <p>
     * Stops searching on first match.
     *
     * @param color Capturing color
     * @return {@code true} if a move was found, {@code false} otherwise
     */
    public Optional<Coordinates> findAnyValidMove(Color color) {
        return findAllValidMoves(color).findAny();
    }

    /**
     * Finds all valid moves for {@code color}.
     *
     * @param color Color to find moves for
     * @return Stream of coordinates representing valid moves
     */
    public Stream<Coordinates> findAllValidMoves(Color color) {
        return edges.stream()
            .filter(c -> isValidMove(color, c));
    }

    /**
     * Initializes edges.
     */
    private void initializeEdges() {
        board.traverse()
            .filter(c -> board.get(c) != Tile.FREE)
            .forEach(this::updateEdges);
    }

    /**
     * Updates edges with set tile {@code center}.
     *
     * @param center Non free tile
     */
    private void updateEdges(Coordinates center) {
        edges.remove(center);
        Arrays.stream(Direction.values())
            .map(d -> d.next().apply(center))
            .filter(c -> board.validate(c) && board.get(c) == Tile.FREE)
            .forEach(edges::add);
    }

    /**
     * Determines if any enclosing group exists for {@code color} from {@code origin}.
     * <p>
     * Stops searching on first match.
     *
     * @param color Capturing color
     * @param origin Origin to check
     * @return {@code true} if any enclosing group exists, {@code false} otherwise
     *
     * @see #findMatch(Color, Coordinates, Direction)
     */
    private boolean encloseAny(Color color, Coordinates origin) {
        return Arrays.stream(Direction.values())
            .anyMatch(
                d -> findMatch(color, d.next().apply(origin), d)
                    .isPresent()
            );
    }

    /**
     * Finds enclosed tiles for {@code color} from {@code origin}.
     *
     * @param color Capturing color
     * @param origin Origin to check
     * @return Stream of coordinates of enclosed tiles if any
     *
     * @see #encloseAll(Color, Coordinates, Direction)
     */
    private Stream<Coordinates> encloseAll(Color color, Coordinates origin) {
        return Arrays.stream(Direction.values())
            .flatMap(d -> encloseAll(color, d.next().apply(origin), d));
    }

    /**
     * Finds enclosed tiles for {@code color} from {@code start} inclusive in {@code direction}.
     *
     * @param color Capturing color
     * @param start First coordinates to check
     * @return Stream of coordinates of enclosed tiles if any
     *
     * @see #findMatch(Color, Coordinates, Direction)
     */
    private Stream<Coordinates> encloseAll(Color color, Coordinates start, Direction direction) {
        return findMatch(color, start, direction)
            .map(
                m -> Stream.iterate(
                    start,
                    c -> !c.equals(m),
                    direction.next()
                )
            )
            .orElse(Stream.empty());
    }

    /**
     * Finds enclosed group matching tile for {@code color} from {@code start} inclusive in {@code direction}.
     *
     * @param color Capturing color
     * @param start First coordinates to check
     * @param direction Direction to check
     * @return Coordinates of matching tile if any.
     */
    private Optional<Coordinates> findMatch(Color color, Coordinates start, Direction direction) {
        return Stream.iterate(
                start,
                c -> ( board.validate(c) && board.get(c) != Tile.FREE ), // contiguous non-free tiles
                direction.next()
            )
            .filter(c -> board.get(c) == color.tile()) // matches
            .filter(c -> !c.equals(start)) // encloses at least one tile
            .findFirst();
    }
}
