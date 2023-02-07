package org.example.ui.reversi;

import org.example.ui.UserInterfaceSubordinate;

/**
 * Size menu component of {@code StartMenu}.
 */
class SizeMenu extends UserInterfaceSubordinate {
    static private final int DEFAULT_WIDTH = 8;
    static private final int DEFAULT_HEIGHT = 8;

    private int width;
    private int height;

    public SizeMenu(UserInterface main) {
        super(main);
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
    }

    /**
     * @return Width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return Height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Prompts user to set board size.
     */
    public void prompt() {
        System.out.print(
            """
            Please enter new board size.
            (Sizes must be multiples of 2 and greater than or equal to 4)
            """
        );
        width = promptUntil("Width: ", SizeMenu::sizeParser);
        height = promptUntil("Height: ", SizeMenu::sizeParser);
    }

    /**
     * Parses a board side from input.
     * Throws a descriptive {@code IllegalArgumentException} for use with {@code promptUntil} if parsing fails.
     *
     * @param input Input to parse
     * @return Board side
     *
     * @throws IllegalArgumentException If not a number, not multiple of 2 or less than 4
     */
    static private int sizeParser(String input) {
        int size = org.example.ui.UserInterface.intParser(input);
        if ( size < 4 || size % 2 != 0 )
            throw new IllegalArgumentException(
                String.format("Size must be a multiple of 2 greater than or equal to 4 (%d)", size)
            );

        return size;
    }
}
