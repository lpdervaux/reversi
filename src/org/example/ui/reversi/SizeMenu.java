package org.example.ui.reversi;

import org.example.ui.UserInterfaceSubordinate;

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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

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

    static private int sizeParser(String input) {
        int size = org.example.ui.UserInterface.intParser(input);
        if ( size < 4 || size % 2 != 0 ) throw new IllegalArgumentException(
            String.format("Size must be a multiple of 2 greater than or equal to 4 (%d)", size)
        );

        return size;
    }
}
