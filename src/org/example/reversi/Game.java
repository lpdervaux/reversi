package org.example.reversi;

import org.example.board.ordinal.Coordinates;
import org.example.board.ordinal.Direction;
import org.example.board.ordinal.OrdinalBoard;

import java.util.stream.Stream;

/**
 * Game that implements the rules of Reversi.
 */
public class Game {
    /**
     * Builds an {@code OrdinalBoard} of {@code width} and {@code height} with the initial game setup.
     *
     * @param width Multiple of 2 greater than or equal to 4
     * @param height Multiple of 2 greater than or equal to 4
     * @return Correctly initialized board
     *
     * @see #Game(int, int)
     */
    static private OrdinalBoard<Tile> buildInitialBoardState(int width, int height) {
        var initial = new OrdinalBoard<>(Tile.FREE, width, height);

        // set initial center tiles as:
        // w b
        // b w
        var northWest = new Coordinates(initial.getWidth() / 2 - 1, initial.getHeight() / 2 - 1);
        var northEast = Direction.EAST.getNextOperator().apply(northWest);
        var southWest = Direction.SOUTH.getNextOperator().apply(northWest);
        var southEast = Direction.SOUTHEAST.getNextOperator().apply(northWest);
        initial.set(northWest, Tile.WHITE);
        initial.set(southEast, Tile.WHITE);
        initial.set(northEast, Tile.BLACK);
        initial.set(southWest, Tile.BLACK);

        return initial;
    }

    private final Board board;

    private final Player white; // white of this game
    private final Player black; // black of this game

    private int turn; // number of turns elapsed
    private Player currentPlayer; // color for current turn
    private boolean over; // is game over?

    /**
     * Constructs a game of Reversi on a board of {@code width} and {@code height}.
     *
     * @param width Multiple of 2 greater than or equal to 4
     * @param height Multiple of 2 greater than or equal to 4
     * @throws IllegalArgumentException If any size is invalid
     */
    public Game(int width, int height) throws IllegalArgumentException {
        if (
            width < 4
            || height < 4
            || width % 2 != 0
            || height % 2 != 0
        )
            throw new IllegalArgumentException();

        this.board = new Board(buildInitialBoardState(width, height));

        this.white = new Player(Color.WHITE, 2);
        this.black = new Player(Color.BLACK, 2);
        white.versus = black;
        black.versus = white;

        turn = 1;
        currentPlayer = white;
        over = false;
    }

    /**
     * Constructs a game of Reversi on am equal sided board of {@code side}.
     *
     * @param side Multiple of 2 greater than or equal to 4
     * @throws IllegalArgumentException If side is invalid
     */
    public Game(int side) throws IllegalArgumentException {
        this(side, side);
    }

    /**
     * Checks whether {@code move} is valid for next move.
     *
     * @param move Coordinates to check
     * @return {@code true} if coordinates are valid for next move, {@code false} otherwise
     */
    public boolean isValidMove(Coordinates move) {
        return board.isValidMove(currentPlayer.getColor(), move);
    }

    /**
     * @return All valid moves
     */
    public Stream<Coordinates> findValidMoves() {
        return board.findAllValidMoves(currentPlayer.getColor());
    }

    /**
     * Performs next move for current player and updates game state.
     *
     * @param move Next move for current color
     *
     * @throws IllegalStateException If game is over
     * @throws IllegalArgumentException If passed an invalid move
     */
    public void nextMove(Coordinates move) throws IllegalStateException, IllegalArgumentException {
        if ( over ) throw new IllegalStateException();
        if ( !isValidMove(move) ) throw new IllegalArgumentException(); // duplicate check?

        var enclosed = board.nextMove(currentPlayer.getColor(), move);
        updateState(enclosed);
    }

    /**
     * @return Game board width
     */
    public int getWidth() {
        return board.getWidth();
    }

    /**
     * @return Game board height
     */
    public int getHeight() {
        return board.getHeight();
    }

    /**
     * @return White of this game
     */
    public Player getWhite() {
        return white;
    }

    /**
     * @return Black of this game
     */
    public Player getBlack() {
        return black;
    }

    /**
     * @return Turn count
     */
    public int getTurn() {
        return turn;
    }

    /**
     * @return Player for current turn
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * @return {@code true} if game is over, {@code false} otherwise
     */
    public boolean isOver() {
        return over;
    }

    /**
     * @param y Row index
     * @return Ordered stream in column order
     *
     * @throws IndexOutOfBoundsException If index is not within board
     */
    public Stream<Tile> getRow(int y) throws IndexOutOfBoundsException {
        return board.getRow(y);
    }

    /**
     * Updates game state following a move.
     *
     * @param enclosed Number of opposing tiles captured by current player
     *
     * @see #nextMove(Coordinates)
     */
    private void updateState(int enclosed) {
        currentPlayer.score += enclosed + 1;
        currentPlayer.getVersus().score -= enclosed;

        if (
            board.findAnyValidMove(
                    currentPlayer.getVersus().getColor()
                )
                .isPresent()
        ) {
            currentPlayer = currentPlayer.getVersus();
            turn += 1;
        }
        else {
            over = true;
        }
    }
}
