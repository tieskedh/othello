package othello.ai.botwork.evaluators;

import othello.Board;

import java.awt.*;

/**
 * The evaluators are Classes which evaluates a move
 * They get an board and a move and perform calculations on it.
 * Tbey are used by MiniMax and AlphaBetaPruning
 */
public interface Evaluator {
    /**
     * Returns a score of the current move
     * based on the board or the move
     * @param board The board after the move is performed
     * @param side The side which played the move
     * @param move The move being done.
     * @return The score of the move
     */
    public int getScore(Board board, int side, Point move);


}
