package othello.utility;

import java.awt.*;

public class WeightedMove extends Move{

    int score=0;
    public WeightedMove(int player, int location) {
        super(player, location);
    }

    public WeightedMove(int player, Point location) {
        super(player, location);
    }

    public WeightedMove setScore(int score) {
        this.score = score;
        return this;
    }

    public int getScore() {
        return score;
    }
}
