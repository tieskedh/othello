package othello.ai.evaluators;

import othello.Board;

/**
 * Created by thijs on 11-4-2016.
 */
public class PieceCountEvaluator implements Evaluator{
    @Override
    public int getScore(Board board, int side) {
        return board.getOccurrences(side);
    }
}
