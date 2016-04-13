package othello.ai.botwork.evaluators;

import othello.Board;

import java.awt.*;

/**
 * Returns the amount of possible moves for side
 */
public class PossibleMoveEvaluator implements Evaluator{

    @Override
    public int getScore(Board board, int side, Point move) {
        return board.getPossibleMoves(side).length;
    }
}