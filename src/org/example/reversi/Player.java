package org.example.reversi;

/**
 * Represents white or black for a specific {@code Game} with associated {@code Color}, opponent and score.
 */
// TODO: may be an inner static class of Game; Game is already fairly large however
public class Player {
    private final Color color; // color
    protected Player versus; // opponent
    protected int score; // score

    // should only be instantiated by Game
    protected Player(Color color, int score) {
        this.color = color;
        this.score = score;
    }

    /**
     * @return {@code Player}'s color
     */
    public Color color() {
        return color;
    }

    /**
     * @return Opposing {@code Player}
     */
    public Player versus() {
        return versus;
    }

    /**
     * @return {@code Player}'s score
     */
    public int score() {
        return score;
    }
}
