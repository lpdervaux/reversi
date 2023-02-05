package org.example.reversi;

import org.example.board.ordinal.Coordinates;
import org.example.board.ordinal.Direction;
import org.example.board.ordinal.OrdinalBoard;

import java.util.*;
import java.util.stream.Stream;

/**
 * Mutable state class that implements the rules of Reversi.
 */
// TODO: class is fairly large; it could be split into a hierarchy of abstract classes - said abstract classes would make no sense in isolation however
// TODO: maintaining a set of all valid moves each turn is a bottleneck for larger grids, stop tracking them; keep track of the single move found by validMoveAny instead
public class Game {
    //
    // Instance variables
    //

    private final OrdinalBoard<Tile> board; // game board
    private final Player white; // white of this game
    private final Player black; // black of this game
    private final Set<Coordinates> validMoves; // set of valid moves for current turn
    private final Set<Coordinates> edges; // set of edges

    private int turn; // number of turns elapsed
    private Player current; // color for the current turn
    private boolean skipped; // was last color's turn skipped?
    private boolean over; // is the game over?

    //
    // Constructors
    //

    /**
     * Instantiates a new {@code Game} with a board of {@code width} and {@code height}.
     *
     * @param width Multiple of 2, greater or equal to 4
     * @param height Multiple of 2, greater or equal to 4
     *
     * @throws IllegalArgumentException If any of width and height are invalid
     */
    public Game(int width, int height) {
        if (
            width % 2 != 0 || height % 2 != 0
                || width < 4 || height < 4
        ) throw new IllegalArgumentException();

        // create board from a fixed size List
        this.board = new OrdinalBoard<>(
            width, height
        );

        // create validMoves and edges
        this.validMoves = new HashSet<>(board.width() * board.height() / 2);
        this.edges = new HashSet<>(board.width() * board.height() / 2);

        // create players
        this.white = new Player(Color.WHITE, 0);
        this.black = new Player(Color.BLACK, 0);
        initializePlayersCrossReference();

        initializeState();
    }

    //
    // Initializers
    //

    /**
     * Initializes black and white's cross-references.
     * <p>
     * Composed method of {@code Game(int, int)}
     *
     * @see #Game(int, int)
     */
    private void initializePlayersCrossReference() {
        white.versus = black;
        black.versus = white;
    }

    /**
     * Initializes game state.
     */
    private void initializeState() {
        initializeTurnState();
        initializeBoard();
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
     * Sets board to its initial state and updates edges and scores accordingly.
     * <p>
     * Composed method of {@code initializeState}
     *
     * @see #initializeState()
     */
    private void initializeBoard() {
        board.traverse().forEach(c -> board.set(c, Tile.FREE));

        // initial center color tiles such as
        // w b
        // b w
        var northWest = new Coordinates(board.width() / 2 - 1, board.height() / 2 - 1);
        var northEast = Direction.EAST.next().apply(northWest);
        var southWest = Direction.SOUTH.next().apply(northWest);
        var southEast = Direction.SOUTHEAST.next().apply(northWest);
        board.set(northWest, Tile.WHITE);
        board.set(southEast, Tile.WHITE);
        board.set(northEast, Tile.BLACK);
        board.set(southWest, Tile.BLACK);

        white.score = 2;
        black.score = 2;

        edges.clear();
        updateEdges(northWest);
        updateEdges(northEast);
        updateEdges(southWest);
        updateEdges(southEast);
    }

    //
    // State change and update methods
    //

    /**
     * Updates edges after setting a tile.
     *
     * @param set Coordinates of a tile that was set
     */
    private void updateEdges(Coordinates set) {
        edges.remove(set);
        Arrays.stream(Direction.values())
            .map(d -> d.next().apply(set))
            .filter(c -> board.validate(c) && board.get(c) == Tile.FREE)
            .forEach(edges::add);
    }

    /**
     * Updates validMoves set in place, according to current game state.
     */
    private void updateValidMoves() {
        validMoves.clear();
        validMoveAll(current.color())
            .forEach(validMoves::add);
    }

    /**
     * Sets tile at {@code move} and enclosing groups for current color, then update game state.
     *
     * @param move Coordinates of move to set
     */
    private void setAndUpdate(Coordinates move) {
        var enclosed = set(current.color(), move);
        updateEdges(move);
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
    private Stream<Coordinates> validMoveAll(Color color) {
        return edges.stream()
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
    private boolean validMoveAny(Color color) {
        return edges.stream()
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
    // TODO: version of enclose that finds first match and returns true or false
    // TODO: maybe make a non stream based version also, as a point of comparison
    private Stream<Coordinates> enclose(Color color, Coordinates origin, Direction direction) {
        return Stream.iterate(
                direction.next().apply(origin),
                c -> ( board.validate(c) && board.get(c) != Tile.FREE ),
                direction.next()
            ) // contiguous non-free tiles
            .filter(c -> board.get(c) == color.tile())
            .findFirst() // first matching tile
            .map(
                m -> Stream.iterate(
                    direction.next().apply(origin),
                    c -> !c.equals(m),
                    direction.next()
                )
            ) // tiles between origin and match
            .orElse(Stream.empty()); // or nothing
    }

    //
    // Public methods
    //

    /**
     * Resets the game to its initial state.
     */
    public void reset() {
        initializeState();
    }

    /**
     * Checks whether {@code move} is valid for next move.
     *
     * @param move Coordinates to check
     * @return {@code true} if coordinates are valid for next move, {@code false} otherwise
     */
    public boolean validate(Coordinates move) {
        return validMoves.contains(move);
    }

    /**
     * Performs next move for current player and updates game state.
     *
     * @param move Next move for current color
     *
     * @throws IllegalStateException If game is over
     * @throws IllegalArgumentException If passed an invalid move
     */
    public void next(Coordinates move) throws IllegalStateException, IllegalArgumentException {
        if ( over ) throw new IllegalStateException();
        if ( !validate(move) ) throw new IllegalArgumentException();

        setAndUpdate(move);
    }

    //
    // Public accessors
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
    // Display helper methods
    //

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
