package org.example.ui.reversi;

import org.example.board.ordinal.Coordinates;
import org.example.reversi.Color;
import org.example.reversi.Game;
import org.example.reversi.ai.RandomAI;

/**
 * Simple text interface for Reversi.
 * <p>
 * User may choose board size, tile set, and assign colors to AI or human control.
 * A {@code Game} is then created and played to the end.
 */
public class UserInterface extends org.example.ui.UserInterface {
    private final StartMenu startMenu;

    private Game game;

    /**
     * Instantiates {@code UserInterface} with default parameters.
     */
    public UserInterface() {
        this.startMenu = new StartMenu(this);
    }

    /**
     * Starts user interface and returns upon game end.
     */
    public void start() {
        displayStartupMessage();
        startMenu.promptUntilStart();

        game = new Game(
            startMenu.getSizeMenu().getWidth(),
            startMenu.getSizeMenu().getHeight()
        );

        inputLoop();
    }

    /**
     * Displays startup message.
     * Called once at start.
     *
     * @see #start()
     */
    private void displayStartupMessage() {
        System.out.println("Welcome to the Reversi console interface.");
    }

    /**
     * Displays game state followed by current player.
     * Called for every iteration of {@code gameInputLoop}.
     *
     * @see #inputLoop()
     */
    private void displayCurrentState() {
        displayState();
        System.out.printf("Player: %s%n", game.getCurrentPlayer().getColor());
    }

    /**
     * Displays next move.
     * Called for every iteration of {@code gameInputLoop}.
     *
     * @param move Next move
     *
     * @see #inputLoop()
     */
    private void displayNextMove(Coordinates move) {
        System.out.printf("Move: [%d, %d]%n", move.x(), move.y());
    }

    /**
     * Displays game state followed by winner.
     * Called by {@code gameInputLoop} after game ends.
     *
     * @see #inputLoop()
     */
    private void displayFinalState() {
        displayState();

        if ( game.getWhite().getScore() == game.getBlack().getScore() )
            System.out.println("Draw");
        else
            System.out.printf(
                "Winner: %s%n",
                ( game.getWhite().getScore() > game.getBlack().getScore() ) ? Color.WHITE : Color.BLACK
            );
    }

    /**
     * Displays game state as indexed grid, turn count and score.
     * <p>
     * Composed function of {@code displayCurrentState} and {@code displayFinalState}.
     *
     * @see #displayCurrentState()
     * @see #displayFinalState()
     */
    // TODO: Console IO is a bottleneck for large board size, add an option to only display turns or only final result for AI matches
    private void displayState() {
        System.out.println(buildIndexedGrid());
        System.out.printf(
            "Turn %d (W %d B %d)%n",
            game.getTurn(), game.getWhite().getScore(), game.getBlack().getScore()
        );
    }

    //
    // String building methods for use with display methods
    //

    /**
     * Builds a {@code String} representing the game board as a single-digit indexed grid.
     * <p>
     * Composed function of {@code displayState}.
     *
     * @return Game board as string
     *
     * @see #displayState()
     */
    private String buildIndexedGrid() {
        return new GameGridBuilder(game, startMenu.getTileMapMenu().getTileMap()).toString();
    }


    //
    // Prompt related methods
    //

    /**
     * Prompts for a move until a valid move for the current game state is input.
     *
     * @return Coordinates of a valid move.
     */
    private Coordinates promptForNextMoveUntilValid() {
        Coordinates move;

        System.out.println("Please input next move");
        move = promptForNextMove();

        while ( !game.isValidMove(move) ) {
            System.out.printf("[%d, %d] is not a valid move%n", move.x(), move.y());
            move = promptForNextMove();
        }

        return move;
    }

    /**
     * Prompts for a single move.
     * <p>
     * Composed function of {@code promptForNextMoveUntilValid}.
     *
     * @return Coordinates of a move.
     *
     * @see #promptForNextMoveUntilValid()
     */
    private Coordinates promptForNextMove() {
        int x = promptUntil("x: ", s -> coordinateParser(s, game.getWidth()) );
        int y = promptUntil("y: ", s -> coordinateParser(s, game.getHeight()) );

        return new Coordinates(x, y);
    }

    //
    // Parser methods for use with prompt methods
    //

    /**
     * Parses a coordinate from input.
     * Throws a descriptive {@code IllegalArgumentException} for use with {@code promptUntil} if parsing fails.
     * <p>
     * Composed function of {@code promptForNextMove}.
     *
     * @param input Input to parse
     * @param limit Upper bound for coordinate
     * @return Coordinate
     *
     * @throws IllegalArgumentException If not a number or not within bounds
     *
     * @see #promptForNextMove()
     */
    private int coordinateParser(String input, int limit) {
        int x = org.example.ui.UserInterface.intParser(input);
        if ( x < 0 || x >= limit ) throw new IllegalArgumentException(
            String.format("%d is not within the board", x)
        );

        return x;
    }

    //
    // Input loop and query methods
    //

    /**
     * Displays game state, queries and executes a move until {@code game} ends, then displays final state.
     */
    private void inputLoop() {
        do {
            System.out.println(); // blank line separator
            displayCurrentState();

            Coordinates nextMove = queryNextMove();
            displayNextMove(nextMove);

            game.nextMove(nextMove);
        } while ( !game.isOver() );

        System.out.println(); // blank line separator
        displayFinalState();
    }

    /**
     * Queries either human or AI for the next move depending on the {@code aiActive} status for current player's {@code Color}.
     *
     * @return Coordinates of next move
     */
    private Coordinates queryNextMove() {
        Coordinates nextMove;

        if (
            game.getCurrentPlayer().getColor() == Color.WHITE && startMenu.getPlayerMenu().isWhiteAIActive()
            || game.getCurrentPlayer().getColor() == Color.BLACK && startMenu.getPlayerMenu().isBlackAIActive()
        ) {
            nextMove = RandomAI.nextMove(game); // demo only supports RandomAI
        }
        else {
            nextMove = promptForNextMoveUntilValid();
        }

        return nextMove;
    }
}
