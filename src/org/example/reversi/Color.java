package org.example.reversi;

/**
 * Colors of Reversi, with associated tile and opposite.
 */
public enum Color {
    WHITE(Tile.WHITE),
    BLACK(Tile.BLACK);

    private final Tile tile;
    private Color versus;

    static {
        WHITE.versus = BLACK;
        BLACK.versus = WHITE;
    }

    Color(Tile tile) {
        this.tile = tile;
    }

    public Tile tile() {
        return tile;
    }

    public Color versus() {
        return versus;
    }
}
