package org.example.board.ordinal;

import java.util.function.UnaryOperator;

/**
 * Represents directions relative to {@code OrdinalBoard}. Cardinals are such that {@code Coordinates} 0, 0 is the northeasternmost point.
 */
public enum Direction {
    NORTH(
        c -> new Coordinates(c.x(), c.y() - 1)
    ),
    NORTHEAST(
        c -> new Coordinates(c.x() + 1, c.y() - 1)
    ),
    EAST(
        c -> new Coordinates(c.x() + 1, c.y())
    ),
    SOUTHEAST(
        c -> new Coordinates(c.x() + 1, c.y() + 1)
    ),
    SOUTH(
        c -> new Coordinates(c.x(), c.y() + 1)
    ),
    SOUTHWEST(
        c -> new Coordinates(c.x() - 1, c.y() + 1)
    ),
    WEST(
        c -> new Coordinates(c.x() - 1, c.y())
    ),
    NORTHWEST(
        c -> new Coordinates(c.x() - 1, c.y() - 1)
    );

    private final UnaryOperator<Coordinates> next; // function to produce the next set of coordinates in direction
    private Direction versus; // may not be final since self references can only be initialized in a static block

    Direction(UnaryOperator<Coordinates> next) {
        this.next = next;
    }

    static {
        NORTH.versus = SOUTH;
        SOUTH.versus = NORTH;
        EAST.versus = WEST;
        WEST.versus = EAST;
        NORTHEAST.versus = SOUTHWEST;
        SOUTHWEST.versus = NORTHEAST;
        NORTHWEST.versus = SOUTHEAST;
        SOUTHEAST.versus = NORTHWEST;
    }

    /**
     * @return A function that produces the next set of {@code Coordinates}
     */
    public UnaryOperator<Coordinates> next() {
        return next;
    }

    /**
     * @return The opposite direction
     */
    public Direction versus() {
        return versus;
    }
}
