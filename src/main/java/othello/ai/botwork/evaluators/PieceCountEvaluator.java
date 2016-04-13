package othello.ai.botwork.evaluators;

import othello.Board;

import java.awt.*;


/**
 * The Class PieceCountEvaluator.
 */
public class PieceCountEvaluator implements Evaluator{
    
    /* (non-Javadoc)
     * @see othello.ai.botwork.evaluators.Evaluator#getScore(othello.Board, int, java.awt.Point)
     */
    @Override
    public int getScore(Board board, int side, Point move) {
        return board.getOccurrences(side);
    }
}
