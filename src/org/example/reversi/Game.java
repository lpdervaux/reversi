package org.example.reversi;

import org.example.board.ordinal.Coordinates;
import org.example.board.ordinal.Direction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Mutable state class that implements the rules of Reversi.
 */
// TODO: class is fairly large; it could be split into a hierarchy of abstract classes - said abstract classes would make no sense in isolation however
// TODO: maintain and use a Set of edge tiles
public class Game {
    //
    // Instance variables
    //

    // TODO: replace Board with OrdinalBoard<Tile>
    private final Board board; // backing board
    private final Player white; // white of this game
    private final Player black; // black of this game
    private final List<Coordinates> validMoves; // list of valid moves for current turn

    private int turn; // number of turns elapsed
    private Player current; // color for the current turn
    private boolean skipped; // was last color's turn skipped?
    private boolean over; // is the game over?

    //
    // Constructors
    //

    /**
     * Instantiates a new {@code Game} using a dedicated {@code board}.
     * Caller is responsible for ensuring that {@code board} will not be externally accessed until this instance is reclaimed.
     *
     * @param board Board for use with a new {@code Game} instance
     */
    // TODO: remove this constructor and move logic into Game(int, int)
    private Game(Board board) {
        this.board = board;
        this.white = new Player(Color.WHITE, 0);
        this.black = new Player(Color.BLACK, 0);
        this.validMoves = new ArrayList<>(board.width() * board.height() / 2);
        initializeColorsVersus();

        initializeState();
    }

    /**
     * Instantiates a new {@code Game} with a {@code Board} of {@code width} and {@code height}.
     *
     * @param width Multiple of 2, greater or equal to 4
     * @param height Multiple of 2, greater or equal to 4
     */
    public Game(int width, int height) {
        this(Board.create(width, height));
    }

    //
    // Initializers
    //

    /**
     * Initializes black and white's cross-references.
     * <p>
     * Composed method of {@code Game(Board board)}
     *
     * @see #Game(Board board)
     */
    private void initializeColorsVersus() {
        white.versus = black;
        black.versus = white;
    }

    /**
     * Initializes game state.
     */
    private void initializeState() {
        initializeTurnState();
        initializeBoardAndScore();
        updateValidMoves();
    }

    /**
     * Sets instance variables to their initial state.
     * <p>
     * Composed method of {@code initializeState}
     *
     * @see #initializeState()
     */
    private void initializeTurnState() {
        over = false;
        skipped = false;
        turn = 1;
        current = white;
    }

    /**
     * Sets board to its initial state and updates white and black's scores accordingly.
     * <p>
     * Composed method of {@code initializeState}
     *
     * @see #initializeState()
     */
    private void initializeBoardAndScore() {
        board.traverse().forEach(c -> board.set(c, Tile.FREE));

        // initial center color tiles such as
        // w b
        // b w
        var topLeft = new Coordinates(board.width() / 2 - 1, board.height() / 2 - 1);
        board.set(topLeft, Tile.WHITE);
        board.set(Direction.SOUTHEAST.next().apply(topLeft), Tile.WHITE);
        board.set(Direction.EAST.next().apply(topLeft), Tile.BLACK);
        board.set(Direction.SOUTH.next().apply(topLeft), Tile.BLACK);

        white.score = 2;
        black.score = 2;
    }

    //
    // State change and update methods
    //

    /**
     * Updates validMoves list in place, according to current game state.
     */
    private void updateValidMoves() {
        validMoveAll(current.color())
            .collect(
                () -> { validMoves.clear(); return validMoves; },
                List::add,
                List::addAll
            );
    }

    /**
     * Sets tile at {@code move} and enclosing groups for current color, then update game state.
     *
     * @param move Coordinates of move to set
     */
    private void setAndUpdateState(Coordinates move) {
        var enclosed = set(current.color(), move);
        updateState(enclosed);
    }

    /**
     * Sets tile at {@code origin} and all enclosing groups to {@code color}'s tile.
     * <p>
     * Composed method of {@code setAndUpdateState}
     *
     * @param color Color to set tile for
     * @param origin Coordinates of move
     * @return Number of opposing tiles enclosed
     *
     * @see #encloseAll(Color, Coordinates)
     */
    private int set(Color color, Coordinates origin) {
        board.set(origin, color.tile());

        return encloseAll(color, origin)
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
     * Updates game state following a move.
     * <p>
     * Composed method of {@code setAndUpdateState}
     *
     * @param enclosed Number of opposing tiles captured by current player
     */
    private void updateState(int enclosed) {
        current.score += enclosed + 1;
        current.versus().score -= enclosed;

        if ( validMoveAny(current.versus().color()) ) {
            skipped = false;
            current = current.versus();
            turn += 1;
            updateValidMoves();
        }
        else if ( validMoveAny(current.color()) ) {
            skipped = true;
            turn += 1;
            updateValidMoves();
        }
        else {
            over = true;
            skipped = false;
            updateValidMoves();
        }
    }

    //
    // Move validation logic methods
    //

    /**
     * Finds all valid moves for {@code color}.
     *
     * @param color Color to find moves for
     * @return Stream of coordinates representing valid moves
     *
     * @see #validMove(Color, Coordinates)
     */
    // TODO: speed this up with edge set
    private Stream<Coordinates> validMoveAll(Color color) {
        return board.traverse()
            .filter(c -> validMove(color, c));
    }

    /**
     * Finds any valid move for {@code color}.
     * Stops searching on first match.
     *
     * @param color Color to find a move for
     * @return {@code true} if a move was found, {@code false} otherwise
     *
     * @see #validMove(Color, Coordinates)
     */
    // TODO: speed this up with edge Set
    private boolean validMoveAny(Color color) {
        return board.traverse()
            .anyMatch(c -> validMove(color, c));
    }

    /**
     * Determines whether a move by {@code color} at {@code origin} would be valid.
     * A valid move is one that targets a free tile and would create one or more enclosing group.
     *
     * @param color Color to check move for
     * @param origin Coordinates of move
     * @return {@code true} if valid, {@code false} otherwise
     *
     * @see #encloseAny(Color, Coordinates)
     */
    private boolean validMove(Color color, Coordinates origin) {
        boolean valid;

        if ( board.get(origin) != Tile.FREE )
            valid = false;
        else
            valid = encloseAny(color, origin);

        return valid;
    }

    //
    // Tile enclosing logic methods
    //

    /**
     * Determines if any enclosing group would be created if {@code origin} was set to {@code color}'s matching tile.
     * Stops searching on first match.
     *
     * @param color Color to check move for
     * @param origin Coordinates of move
     * @return {@code true} if at least one enclosing group would be created, {@code false} otherwise
     *
     * @see #enclose(Color, Coordinates, Direction)
     */
    private boolean encloseAny(Color color, Coordinates origin) {
        return Arrays.stream(Direction.values())
            .anyMatch(
                d -> enclose(color, origin, d)
                    .findAny()
                    .isPresent()
            );
    }

    /**
     * Finds coordinates of all tiles that would be enclosed if {@code origin} was set to {@code color}'s matching tile.
     *
     * @param color Color to check move for
     * @param origin Coordinates of move
     * @return Stream of coordinates of all opposing tiles that would be enclosed, which may be empty
     *
     * @see #enclose(Color, Coordinates, Direction)
     */
    private Stream<Coordinates> encloseAll(Color color, Coordinates origin) {
        return Arrays.stream(Direction.values())
            .flatMap(d -> enclose(color, origin, d));
    }

    /**
     * Finds tiles that would be enclosed in {@code direction} if {@code origin} was set to {@code color}'s matching tile.
     *
     * @param color Color of tile to set
     * @param origin Origin coordinates
     * @param direction Direction to check
     * @return Stream of coordinates of enclosed opposing tiles, which may be empty.
     */
    // TODO: change this logic to use multiple takeWhile
    private Stream<Coordinates> enclose(Color color, Coordinates origin, Direction direction) {
        return board.direction(origin, direction)
            .skip(1) // skip origin tile
            .takeWhile(c -> board.get(c) != Tile.FREE) // reduce to coordinates of contiguous non-FREE tiles
            .filter(c -> board.get(c) == color.tile())
            .findFirst() // reduce to coordinates of the first matching tile of color
            .map(
                match -> Stream.iterate(
                    origin,
                    c -> !c.equals(match),
                    direction.next()
                )
                .skip(1)
            ) // map to stream of coordinates from origin to match
            .orElse(Stream.empty()); // or an empty stream if no match was found
    }

    //
    // Public methods
    //

    //
    // Public accessors
    //

    /**
     * @return White of this game
     */
    public Player white() {
        return white;
    }

    /**
     * @return Black of this game
     */
    public Player black() {
        return black;
    }

    /**
     * @return Turn count
     */
    public int turn() {
        return turn;
    }

    /**
     * @return Player for current turn
     */
    public Player currentPlayer() {
        return current;
    }

    /**
     * @return A list of all valid moves for the current turn
     */
    public List<Coordinates> validMoves() {
        return List.copyOf(validMoves);
    }

    /**
     * @return {@code true} if a player had no valid move after previous turn, {@code false} otherwise
     */
    public boolean skipped() {
        return skipped;
    }

    /**
     * @return {@code true} if game is over, {@code false} otherwise
     */
    public boolean over() {
        return over;
    }

    //
    // Public state change methods
    //

    /**
     * Resets the game to its initial state.
     */
    public void reset() {
        initializeState();
    }

    /**
     * Convenience method, calls {@code next(Coordinates)} with provided coordinates.
     *
     * @param x Horizontal axis coordinate
     * @param y Vertical axis coordinate
     *
     * @throws IllegalStateException If game is over
     * @throws IllegalMoveException If passed an invalid move
     *
     * @see #next(Coordinates)
     */
    // TODO: method from early development, to be removed
    public void next(int x, int y) {
        next(new Coordinates(x, y));
    }

    /**
     * Performs next move for current player and updates game state.
     *
     * @param move Next move for current color
     *
     * @throws IllegalStateException If game is over
     * @throws IllegalMoveException If passed an invalid move
     */
    public void next(Coordinates move) throws IllegalStateException, IllegalMoveException {
        if ( over ) throw new IllegalStateException();
        if ( !validate(move) ) throw new IllegalMoveException(move);

        setAndUpdateState(move);
    }

    /**
     * Checks whether {@code move} is valid for next move.
     *
     * @param move Coordinates to check
     * @return {@code true} if coordinates are valid for next move, {@code false} otherwise
     */
    public boolean validate(Coordinates move) {
        return validMoves.stream()
            .anyMatch(c -> c.equals(move));
    }

    //
    // Display helper methods
    //

    /**
     * @return Game board width
     */
    public int width() {
        return board.width();
    }

    /**
     * @return Game board height
     */
    public int height() {
        return board.height();
    }

    /**
     * @return Ordered stream of board tiles in row-major order
     */
    public Stream<Tile> stream() {
        return board.stream();
    }

    /**
     * @param y Row index
     * @return Ordered stream of row {@code y} in column order
     *
     * @throws IndexOutOfBoundsException If index is not within the board
     */
    public Stream<Tile> row(int y) {
        return board.row(y);
    }

    /**
     * @param x Column index
     * @return Ordered stream of column {@code x} in row order
     *
     * @throws IndexOutOfBoundsException If index is not within the board
     */
    public Stream<Tile> column(int x) {
        return board.column(x);
    }
}
