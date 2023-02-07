package org.example.ui.reversi;

import org.example.board.ordinal.Coordinates;
import org.example.reversi.Color;
import org.example.reversi.Game;
import org.example.reversi.Tile;
import org.example.reversi.ai.RandomAI;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Simple text interface for Reversi.
 * <p>
 * User may choose board size and tile set, and assign colors to AI or human control.
 * A {@code Game} is then created and played to the end.
 */
// TODO: move menus into its their own class, compose it into this
public class UserInterface extends org.example.ui.UserInterface {
    static private final int DEFAULT_WIDTH = 8;
    static private final int DEFAULT_HEIGHT = 8;

    // menus
    static private final Map<String, String> START_MENU;
    static private final Map<String, String> PLAYER_MENU;
    static private final Map<String, String> TILE_MAP_MENU;

    static {
        // LinkedHashMap to preserve ordering
        START_MENU = new LinkedHashMap<>(4, 1.0f);
        START_MENU.put("b", "Board size");
        START_MENU.put("p", "Players");
        START_MENU.put("t", "Tile map");
        START_MENU.put("s", "Start");

        PLAYER_MENU = new LinkedHashMap<>(2, 1.0f);
        PLAYER_MENU.put("h", "Human");
        PLAYER_MENU.put("a", "AI");

        TILE_MAP_MENU = new LinkedHashMap<>(2, 1.0f);
        TILE_MAP_MENU.put("a", "ASCII: w b .");
        TILE_MAP_MENU.put("d", "Dot: o ● ·");
    }

    //
    // non-static
    //

    //
    // Instance variables
    //

    private int width; // board width option
    private int height; // board height option
    private Map<Tile, String> tileMap; // tile map to display game board with
    private final Map<Color, Boolean> aiActive; // maps each Color to a boolean for which true equates to AI controlled

    private Game game;

    //
    // Instance methods
    //

    /**
     * Instantiates {@code UserInterface} with default parameters.
     */
    public UserInterface() {
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;

        this.aiActive = new EnumMap<>(Color.class);
        this.aiActive.put(Color.WHITE, true);
        this.aiActive.put(Color.BLACK, true);

        this.tileMap = TileMaps.DOT_TILE_MAP;
    }

    /**
     * Starts user interface and returns upon game end.
     */
    public void start() {
        displayStartupMessage();
        promptStartMenuUntilStart();

        game = new Game(width, height);
        gameInputLoop();
    }

    //
    // Display related methods
    //

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
     * @see #gameInputLoop()
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
     * @see #gameInputLoop()
     */
    private void displayNextMove(Coordinates move) {
        System.out.printf("Move: [%d, %d]%n", move.x(), move.y());
    }

    /**
     * Displays game state followed by winner.
     * Called by {@code gameInputLoop} after game ends.
     *
     * @see #gameInputLoop()
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
    // TODO: put builder back to itw owm class
    private String buildIndexedGrid() {
        var capacity = (game.getWidth() + 2) * 2 * (game.getHeight() + 1) + 10;
        var builder = new StringBuilder(capacity);

        // header
        builder.append("  ");
        IntStream.range(0, game.getWidth())
            .forEachOrdered(
                x -> {
                    builder.append(x % 10);
                    builder.append(" ");
                }
            );
        builder.append(System.lineSeparator());

        // rows
        IntStream.range(0, game.getHeight())
            .forEachOrdered(
                y -> {
                    builder.append(y % 10);
                    builder.append(" ");

                    game.getRow(y)
                        .forEachOrdered(
                            t -> {
                                builder.append(tileMap.get(t));
                                builder.append(" ");
                            }
                        );

                    builder.append(System.lineSeparator());
                }
            );

        return builder.toString();
    }


    //
    // Prompt related methods
    //

    /**
     * Prompts for start menu and processes selections until start is selected.
     */
    private void promptStartMenuUntilStart() {
        String choice;

        do {
            choice = promptStartMenu();
            switch (choice) {
                case "b" -> promptAndChangeBoardSize();
                case "p" -> promptAndChangePlayerAI();
                case "t" -> promptAndChangeTileMap();
            }
        } while ( !choice.equals("s") );
    }

    /**
     * Displays options and prompts for start menu selection.
     *
     * @return Start menu choice
     *
     * @see #promptStartMenuUntilStart()
     */
    private String promptStartMenu() {
        return promptUntilMenuChoice(
            String.format(
                """
                [Options]
                Board: %d x %d
                Players:
                    White (%s) Black (%s)
                Tiles: %s
                """,
                width, height,
                aiActive.get(Color.WHITE) ? "AI" : "Human", aiActive.get(Color.BLACK) ? "AI" : "Human",
                tileMap.values().stream()
                    .map(s -> String.format("%s ", s))
                    .collect(Collectors.joining())
            ),
            START_MENU
        );
    }

    /**
     * Prompts for board size and assigns {@code width} and {@code height} accordingly.
     *
     * @see #promptStartMenuUntilStart()
     */
    private void promptAndChangeBoardSize() {
        System.out.print(
            """
            Please enter new board size.
            (Sizes must be multiples of 2 and greater than or equal to 4)
            """
        );
        width = promptUntil("Width: ", this::sizeParser);
        height = promptUntil("Height: ", this::sizeParser);
    }

    /**
     * Prompts for player settings and assigns accordingly.
     *
     * @see #promptStartMenuUntilStart()
     */
    private void promptAndChangePlayerAI() {
        aiActive.put(Color.WHITE, promptPlayerAI("Configure white" + System.lineSeparator()));
        aiActive.put(Color.BLACK, promptPlayerAI("Configure black" + System.lineSeparator()));
    }

    /**
     * Prompts for tile map and assigns accordingly.
     *
     * @see #promptStartMenuUntilStart()
     */
    private void promptAndChangeTileMap() {
        var tile = promptUntilMenuChoice("Please select a tile map." + System.lineSeparator(), TILE_MAP_MENU);

        switch (tile) {
            case "a" -> tileMap = TileMaps.ASCII_TILE_MAP;
            case "d" -> tileMap = TileMaps.DOT_TILE_MAP;
        }
    }

    /**
     * Prompts for a single player setting.
     * <p>
     * Composed function of {@code promptAndChangePlayerAI}.
     *
     * @param prompt Prompt to display
     * @return {@code true} for AI controlled, {@code false} otherwise
     *
     * @see #promptAndChangePlayerAI()
     */
    private boolean promptPlayerAI(String prompt) {
        var choice = promptUntilMenuChoice(prompt, PLAYER_MENU);
        return ( choice.equals("a") );
    }

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
        int x = intParser(input);
        if ( x < 0 || x >= limit ) throw new IllegalArgumentException(
            String.format("%d is not within the board", x)
        );

        return x;
    }

    /**
     * Parses a board side from input.
     * Throws a descriptive {@code IllegalArgumentException} for use with {@code promptUntil} if parsing fails.
     * <p>
     * Composed function of {@code promptAndChangeBoardSize}.
     *
     * @param input Input to parse
     * @return Board side
     *
     * @throws IllegalArgumentException If not a number, not multiple of 2 or less than 4
     *
     * @see #promptAndChangeBoardSize()
     */
    private int sizeParser(String input) {
        int size = intParser(input);
        if ( size < 4 || size % 2 != 0 ) throw new IllegalArgumentException(
            String.format("Size must be a multiple of 2 greater than or equal to 4 (%d)", size)
        );

        return size;
    }

    /**
     * Parses a signed integer from input.
     * Throws a descriptive {@code IllegalArgumentException} for use with {@code promptUntil} if parsing fails.
     * <p>
     * Composed function of {@code sizeParser} and {@code coordinateParser}.
     *
     * @param input Input to parse
     * @return Signed integer
     *
     * @throws IllegalArgumentException If not a number
     *
     * @see #sizeParser(String)
     * @see #coordinateParser(String, int)
     */
    private int intParser(String input) {
        int i;

        try {
            i = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                String.format("Not an integer (%s)", input),
                e
            );
        }

        return i;
    }

    //
    // Input loop and query methods
    //

    /**
     * Displays game state, queries and executes a move until {@code game} ends, then displays final state.
     */
    private void gameInputLoop() {
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

        if ( aiActive.get(game.getCurrentPlayer().getColor()) ) {
            nextMove = RandomAI.nextMove(game); // demo only supports RandomAI
        }
        else {
            nextMove = promptForNextMoveUntilValid();
        }

        return nextMove;
    }
}
