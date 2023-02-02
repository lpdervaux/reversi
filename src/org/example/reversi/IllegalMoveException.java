package org.example.reversi;

import org.example.board.ordinal.Coordinates;

/**
 * Exception thrown on illegal move, contains the cause coordinates.
 * {@code RuntimeException} since it may only need checking on user input.
 */
public class IllegalMoveException extends RuntimeException {
    static private final String message = "Illegal move";
    private final Coordinates cause;

    protected IllegalMoveException(Coordinates cause) {
        super(message);
        this.cause = cause;
    }

    /**
     * @return Coordinates that caused this exception
     */
    public Coordinates cause() {
        return cause;
    }
}
