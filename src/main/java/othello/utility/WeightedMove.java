package othello.utility;

import java.awt.*;

/**
 * The Class WeightedMove.
 */
public class WeightedMove extends Move{

    int score=0;
    
    /**
     * Instantiates a new weighted move.
     *
     * @param player the player
     * @param location the location
     */
    public WeightedMove(int player, int location) {
        super(player, location);
    }

    /**
     * Instantiates a new weighted move.
     *
     * @param player the player
     * @param location the location
     */
    public WeightedMove(int player, Point location) {
        super(player, location);
    }

    /**
     * Sets the score.
     *
     * @param score the score
     * @return the weighted move
     */
    public WeightedMove setScore(int score) {
        this.score = score;
        return this;
    }

    /**
     * Gets the score.
     *
     * @return the score
     */
    public int getScore() {
        return score;
    }
}
