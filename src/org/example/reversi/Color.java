package org.example.reversi;

/**
 * Colors of Reversi, with associated tile and opposite.
 */
public enum Color {
    WHITE(Tile.WHITE),
    BLACK(Tile.BLACK);

    private final Tile tile; // associated Tile
    private Color versus; // self-reference may not be final

    // initialize opposing color
    static {
        WHITE.versus = BLACK;
        BLACK.versus = WHITE;
    }

    Color(Tile tile) {
        this.tile = tile;
    }

    /**
     * @return {@code Color}'s associated {@code Tile}
     */
    public Tile tile() {
        return tile;
    }

    /**
     * @return {@code Color}'s opposite
     */
    public Color versus() {
        return versus;
    }
}
