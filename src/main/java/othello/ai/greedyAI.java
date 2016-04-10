package othello.ai;

import othello.Board;
import othello.Game;

/**
 * Created by thijs on 10-4-2016.
 */
public class GreedyAI extends AI {
    public GreedyAI(Game game) {
        super(game, 5);
    }

    @Override
    public int getScore(Board board, int side) {
        return  2*(     board.getOccurrences(side)      -       board.getOccurrences(3-side)     );
    }
}
