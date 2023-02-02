package org.example.ui.reversi;

import org.example.board.ordinal.Coordinates;
import org.example.reversi.Color;
import org.example.reversi.Game;
import org.example.reversi.Tile;
import org.example.reversi.ai.RandomAI;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Simple text interface for Reversi.
 * <p>
 * User may choose board size and tile set, and assign colors to AI or human control.
 * A {@code Game} is then created and played to the end.
 */
public class UserInterface extends org.example.ui.UserInterface {
    // static

    static private final int DEFAULT_WIDTH = 8;
    static private final int DEFAULT_HEIGHT = 8;

    static private final Map<Tile, String> DOT_TILE_MAP =
        Map.ofEntries(
            Map.entry(Tile.WHITE, "o"),
            Map.entry(Tile.BLACK, "●"),
            Map.entry(Tile.FREE, "·")
        );
    static private final Map<Tile, String> ASCII_TILE_MAP =
        Map.ofEntries(
            Map.entry(Tile.WHITE, "w"),
            Map.entry(Tile.BLACK, "b"),
            Map.entry(Tile.FREE, ".")
        );

    // instance

    private int width; // board width
    private int height; // board height
    private Map<Tile, String> tileMap; // tile map to display game board with
    private final Map<Color, Boolean> aiActive; // maps each Color to a boolean for which true equates to AI controlled

    private Game game;

    /**
     * Instantiates {@code UserInterface} with default parameters.
     */
    public UserInterface() {
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;

        this.aiActive = new EnumMap<Color, Boolean>(Color.class);
        this.aiActive.put(Color.WHITE, true);
        this.aiActive.put(Color.BLACK, true);

        this.tileMap = DOT_TILE_MAP;
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
     * Displays game state as indexed grid, turn count and score.
     * Composed function of {@code displayCurrentState} and {@code displayFinalState}.
     *
     * @see #displayCurrentState()
     * @see #displayFinalState()
     */
    private void displayState() {
        System.out.printf(
            """
            %s
            Turn %d (W %d B %d)
            """,
            buildIndexedGrid(),
            game.turn(), game.white().score(), game.black().score()
        );
    }

    /**
     * Displays game state followed by current player.
     * Called for every iteration of {@code gameInputLoop}.
     *
     * @see #gameInputLoop()
     */
    private void displayCurrentState() {
        displayState();
        System.out.printf("Player: %s%n", game.currentPlayer().color());
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
     * Called after game ends.
     *
     * @see #gameInputLoop()
     */
    private void displayFinalState() {
        displayState();

        if ( game.white().score() == game.black().score() )
            System.out.println("Draw");
        else
            System.out.printf(
                "Winner: %s",
                ( game.white().score() > game.black().score() ) ? Color.WHITE : Color.BLACK
            );
    }

    /**
     * Builds a {@code String} representing the game board as a single-digit indexed grid.
     * Composed function of {@code displayState}.
     *
     * @return Game board as string
     *
     * @see #displayState()
     */
    private String buildIndexedGrid() {
        return buildGridHeader() + buildIndexedRows();
    }

    /**
     * Builds a grid header spanning {@code game.width()}.
     * Composed function of {@code buildIndexedGrid}.
     *
     * @return Newline-terminated grid header
     */
    private String buildGridHeader() {
        return String.format(
            "  %s%n",
            IntStream.range(0, game.width())
                .map(x -> x % 10)
                .mapToObj(x -> String.format("%d ", x))
                .collect(Collectors.joining())
        );
    }

    /**
     * Builds an indexed grid of {@code game.height()} where tiles are represented by {@code tileMap}.
     * Composed function of {@code buildIndexedGrid}.
     *
     * @return Newline-terminated indexed rows
     *
     * @see #buildIndexedGrid()
     */
    private String buildIndexedRows() {
        return IntStream.range(0, game.height())
            .mapToObj(
                y -> String.format(
                    "%d %s%n",
                    y % 10,
                    game.row(y)
                        .map(tileMap::get)
                        .map(s -> String.format("%s ", s))
                        .collect(Collectors.joining())
                )
            )
            .collect(Collectors.joining());
    }

    /**
     * Prompts for start menu and processes selections until start is selected.
     */
    private void promptStartMenuUntilStart() {
        StartMenu choice;

        do {
            choice = promptStartMenu();
            switch (choice) {
                case SIZE -> promptAndChangeBoardSize();
                case PLAYER -> promptAndChangePlayerAI();
                case TILE -> promptAndChangeTileMap();
            }
        } while ( choice != StartMenu.START );
    }

    /**
     * Displays options and prompts for start menu selection.
     *
     * @return Start menu choice
     *
     * @see #promptStartMenuUntilStart()
     */
    private StartMenu promptStartMenu() {
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
            StartMenu.class
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
     * Prompts for a single player setting.
     * Composed function of {@code promptAndChangePlayerAI}.
     *
     * @param prompt Prompt to display
     * @return {@code true} for AI controlled, {@code false} otherwise
     *
     * @see #promptAndChangePlayerAI()
     */
    private boolean promptPlayerAI(String prompt) {
        var choice = promptUntilMenuChoice(prompt, PlayerMenu.class);
        return ( choice == PlayerMenu.AI );
    }

    /**
     * Prompts for tile map and assigns accordingly.
     *
     * @see #promptStartMenuUntilStart()
     */
    private void promptAndChangeTileMap() {
        var tile = promptUntilMenuChoice("Please select a tile map." + System.lineSeparator(), TileMapMenu.class);

        switch (tile) {
            case ASCII -> tileMap = ASCII_TILE_MAP;
            case DOT -> tileMap = DOT_TILE_MAP;
        }
    }

    /**
     * Prompts for a move until
     * @return
     */
    private Coordinates promptForNextMoveUntilValid() {
        Coordinates move;

        System.out.println("Please input next move");
        move = promptForNextMove();

        while ( !game.validate(move) ) {
            System.out.printf("[%d, %d] is not a valid move%n", move.x(), move.y());
            move = promptForNextMove();
        }

        return move;
    }

    private Coordinates promptForNextMove() {
        int x = promptUntil("x: ", s -> coordinateParser(s, game.width()) );
        int y = promptUntil("y: ", s -> coordinateParser(s, game.height()) );

        return new Coordinates(x, y);
    }

    private void gameInputLoop() {
        do {
            System.out.println(); // blank line separator
            displayCurrentState();

            Coordinates nextMove = queryNextMove();
            displayNextMove(nextMove);

            game.next(nextMove);
        } while ( !game.over() );

        System.out.println(); // blank line separator
        displayFinalState();
    }

    private Coordinates queryNextMove() {
        Coordinates nextMove;

        if ( aiActive.get(game.currentPlayer().color()) ) {
            nextMove = RandomAI.nextMove(game);
        }
        else {
            nextMove = promptForNextMoveUntilValid();
        }

        return nextMove;
    }

    private int coordinateParser(String input, int limit) {
        int x = intParser(input);
        if ( x < 0 || x >= limit ) throw new IllegalArgumentException(
            String.format("%d is not within the board", x)
        );

        return x;
    }

    private int sizeParser(String input) {
        int size = intParser(input);
        if ( size < 4 || size % 2 != 0 ) throw new IllegalArgumentException(
            String.format("Size must be a multiple of 2 greater than or equal to 4 (%d)", size)
        );

        return size;
    }

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
}
