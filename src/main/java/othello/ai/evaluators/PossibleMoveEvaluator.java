package othello.ai.evaluators;

import othello.Board;
import othello.ai.AI;

/**
 * Created by thijs on 11-4-2016.
 */
public class PossibleMoveEvaluator implements Evaluator{

    @Override
    public int getScore(Board board, int side) {
        return board.getPossibleMoves(side).length;
    }
}
