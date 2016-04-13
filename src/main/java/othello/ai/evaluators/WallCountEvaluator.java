package othello.ai.evaluators;

import othello.Board;

import java.awt.*;

/**
 * Created by thijs on 12-4-2016.
 */
public class WallCountEvaluator implements Evaluator{
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
