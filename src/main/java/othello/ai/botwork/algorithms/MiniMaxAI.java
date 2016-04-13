package othello.ai.botwork.algorithms;

import othello.Board;
import othello.Game;
import othello.utility.WeightedMove;

import java.awt.*;


/**
 * The Class MiniMaxAI.
 */
public class MiniMaxAI extends Algorithm {


    /**
     * Instantiates a new minimax ai.
     *
     * @param game the game
     * @param depth the depth
     */
    public MiniMaxAI(Game game, int depth) {
        super(depth, game);
    }

    /* (non-Javadoc)
     * @see othello.ai.botwork.algorithms.Algorithm#evaluate(othello.Board, int, int, java.awt.Point)
     */
    public WeightedMove evaluate(Board board, int side, int depth, Point move) {
        //new board
        Board tempBoard = new Board(board);
        tempBoard.doMove(move.getLocation(), side);

        //if depth < maxDepth
        if(depth < maxDepth) {

            //for each possible move, get the maximum evaluation
            int opponent = getNextPLayer(board, side);
            if(opponent==0) {
                return new WeightedMove(side, move)
                        .setScore(getScore(board, side, move, depth));
            } else {
                WeightedMove tempMove = progressStream(getLoopingStream(opponent), tempBoard, opponent, depth+1);
                int score = (tempMove==null)? 0 : tempMove.getScore();
                score += getScore(board, side,move, depth);
                return new WeightedMove(side, move).setScore(score);
            }
        }
        if(side==game.getClient()) {
            return new WeightedMove(side, move).setScore(getScore(tempBoard, side, move, depth));
        } else {
            return new WeightedMove(side, move).setScore(-1*getScore(tempBoard, side, move, depth));
        }
    }

}
