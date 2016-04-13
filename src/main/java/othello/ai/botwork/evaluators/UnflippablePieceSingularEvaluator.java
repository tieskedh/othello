package othello.ai.botwork.evaluators;

import othello.Board;
import othello.utility.Move;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by thijs on 11-4-2016.
 */
public class UnflippablePieceSingularEvaluator implements Evaluator, ActionListener, MaxDepthEvaluator{
    int[][] stability;


    Board board;

    public UnflippablePieceSingularEvaluator(Board board, boolean shouldEvaluate) {
        this.board = board;
        board.addActionListener(this);
        prepare();
        if(shouldEvaluate) {
            evaluate();
        }
    }

    private void evaluate() {
        board.forEach((point, integer) -> setAtLocation(point, 0));
    }

    public UnflippablePieceSingularEvaluator(Board board) {
        this(board, true);
    }

    public void prepare() {
        stability = new int[6][8];
        for(int direction = 0; direction < 2; direction++) {
            for(int index = 0; index < 8; index++) {
                stability[direction][index]=8;
            }
        }
        for (int direction = 2; direction < 6; direction++) {
            for(int index = 0; index < 8; index++) {
                stability[direction][index] = index+1;
            }
        }
    }

    protected void setAtLocation(Point location, int player) {
        if(board.getAtLocation(location) != player) {

            int val = (player==0) ? 1 : -1;
            stability[0][location.x] += val; //horizontal
            stability[1][location.y] += val; //vertical
            int diagonalLeftBottom = location.x+location.y;
            if(diagonalLeftBottom < 7) {
                stability[2][diagonalLeftBottom] += val;
            } else if(diagonalLeftBottom == 7) {
                stability[2][7] += val;
                stability[3][7] += val;
            } else {
                stability[3][14-diagonalLeftBottom] += val;
            }
            int diagonalLeftTop = 14-location.x-location.y;
            if(diagonalLeftTop < 7) {
                stability[4][diagonalLeftTop] += val;
            } else if(diagonalLeftTop == 7) {
                stability[4][7] += val;
                stability[5][7] += val;
            } else {
                stability[5][14-diagonalLeftTop] += val;
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Move move = ((Board)e.getSource()).getLastMove();
        setAtLocation(move.getPoint(), move.player);
    }

    private boolean isLocked(Point location) {
        if(! (stability[0][location.x] == 0 && stability[1][location.y] ==0))
            return false;

        int diagonalLeftBottom = location.x+location.y;
        if(diagonalLeftBottom < 7) {
            if(!(stability[2][diagonalLeftBottom] == 0))
                return false;
        } else if(diagonalLeftBottom == 7) {
            if(!(stability[2][7] == 0) && (stability[3][7] == 0))
                return false;
        } else {
            if(!(stability[3][14-diagonalLeftBottom] == 0))
                return false;
        }

        int diagonalLeftTop = 14-location.x-location.y;
        if(diagonalLeftTop < 7) {
            return (stability[4][diagonalLeftTop] == 0);
        } else if(diagonalLeftTop == 7) {
            return (stability[4][7] == 0) && (stability[5][7] == 0);
        } else {
            return stability[5][14-diagonalLeftTop] ==0;
        }
    }

    @Override
    public int getScore(Board board, int side, Point move) {
        System.out.println(board);
        if(move==null) System.out.println("MOVE == NULL");
        return isLocked(move)?1:0;
    }

    @Override
    public int getMaxDepth() {
        return 1;
    }
}
