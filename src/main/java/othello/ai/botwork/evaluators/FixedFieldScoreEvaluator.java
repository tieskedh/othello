package othello.ai.botwork.evaluators;

import othello.Board;

import java.awt.*;

/**
 * Each piece on the board has a fixed value.
 * Returns the assigned value for the place of the move
 */
public class FixedFieldScoreEvaluator implements Evaluator {

    private static final int[][] values = new int[][] {
            { 30, -25, 10, 5, 5, 10, -25,  30},
            {-25, -25,  1, 1, 1,  1, -25, -25},
            { 10,   1,  5, 0, 2,  5,   1,  10},
            {  5,   1,  2, 1, 1,  2,   1,   5},
            {  5,   1,  2, 1, 1,  2,   1,   5},
            { 10,   1,  5, 2, 2,  5,   1,  10},
            {-25, -25,  1, 1, 1,  1, -25, -25},
            {30, -25, 10, 5, 5,  10, -25,  30}
    };

    @Override
    public int getScore(Board board, int side, Point move) {
        return values[move.x][ move.y];
    }
}
