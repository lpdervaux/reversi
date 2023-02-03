package org.example.reversi;

/**
 * Tiles of reversi.
 * Still has associated code point and {@code String} from early development that are to be removed.
 */
// TODO: remove all presentation logic from this
public enum Tile {
    WHITE("o".codePointAt(0)),
    BLACK("●".codePointAt(0)),
    FREE("·".codePointAt(0));

    private final int symbol; // associated code point
    private final String symbolString; // associated String

    Tile(int symbol) {
        this.symbol = symbol;
        this.symbolString = Character.toString(symbol);
    }

    /**
     * @return {@code Tile}'s symbol code point
     */
    public int symbol() {
        return symbol;
    }

    /**
     * @return {@code Tile}'s symbol {@code String}
     */
    public String symbolString() {
        return symbolString;
    }

    /**
     * @return Tile's symbol
     */
    @Override
    public String toString() {
        return symbolString;
    }
}
