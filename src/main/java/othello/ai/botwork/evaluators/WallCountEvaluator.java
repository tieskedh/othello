package othello.ai.botwork.evaluators;

import othello.Board;

import java.awt.*;


/**
 * The Class WallCountEvaluator.
 */
public class WallCountEvaluator implements Evaluator{
    
    /* (non-Javadoc)
     * @see othello.ai.botwork.evaluators.Evaluator#getScore(othello.Board, int, java.awt.Point)
     */
    @Override
    public int getScore(Board board, int side, Point move) {
        int[][] pieces = board.getBoardPieces();
        int score = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if(x==0 || x==7 || y==0 || y==7) {
                    if(pieces[x][y]==side) {
                        score++;
                    } else if(pieces[x][y]==side-2){
                        score-=4;
                    }
                }
            }
        }
        return score;
    }
}
