package othello.utility;

import java.awt.*;

public class WeightedMove extends Move implements Comparable<WeightedMove>{

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

    @Override
    public int compareTo(WeightedMove move) {
        return score - move.getScore();
    }

    @Override
    public WeightedMove setLocation(Point location) {
        super.setLocation(location);
        return this;
    }
}
