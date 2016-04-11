package othello.ai.evaluators;

import othello.Board;

/**
 * Created by thijs on 11-4-2016.
 */
public interface Evaluator {
    public int getScore(Board board, int side);
}
