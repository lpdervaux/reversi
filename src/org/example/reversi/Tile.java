package org.example.reversi;

// TODO: separate representation from logic,
// TODO: should this be an inner class of Board? by the same logic Board should be an inner class of Game... so maybe not
public enum Tile {
    WHITE("o".codePointAt(0)),
    BLACK("●".codePointAt(0)),
    FREE("·".codePointAt(0));

    private final int symbol;
    private final String symbolString;

    Tile(int symbol) {
        this.symbol = symbol;
        this.symbolString = Character.toString(symbol);
    }

    public int symbol() {
        return symbol;
    }

    public String symbolString() {
        return symbolString;
    }

    @Override
    public String toString() {
        return symbolString;
    }
}
