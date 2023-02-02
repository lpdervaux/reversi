package org.example.reversi;

/**
 * Represents white or black for a specific {@code Game}, with associated {@code Color}, opponent and score.
 */
public class Player {
    private final Color color; // color
    protected Player versus; // opponent
    protected int score; // score

    // non publicly instantiable
    protected Player(Color color, int score) {
        this.color = color;
        this.score = score;
    }

    /**
     * @return Player's color
     */
    public Color color() {
        return color;
    }

    /**
     * @return The opposing color
     */
    public Player versus() {
        return versus;
    }

    /**
     * @return Current score of color, which is its number of tiles on board
     */
    public int score() {
        return score;
    }
}
