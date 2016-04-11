package othello.ai.evaluators;

import othello.Board;

import java.awt.*;

/**
 * Created by thijs on 11-4-2016.
 */
public class PieceCountEvaluator implements Evaluator{
    @Override
    public int getScore(Board board, int side, Point move) {
        return board.getOccurrences(side);
    }
}
