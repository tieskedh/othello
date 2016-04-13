package othello.ai.evaluators;

import othello.Board;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Gebruiker on 12/04/2016.
 */
public class UnflippablePieceAreaEvaluator extends MiniMaxEvaluator implements Evaluator, ActionListener {
    private static final int PLAYER_ONE = 1;
    private static final int PLAYER_TWO = 2;
    private boolean[][] stablePieceArrayPlayerOne;
    private boolean[][] stablePieceArrayPlayerTwo;

    UnflippablePieceAreaEvaluator(int maxDepth) {
        super(maxDepth);
    }

    boolean determineIfStable(Point location, int player) {
        int totalvalue = 0;

        totalvalue = countStableSequence(location, -1, -1, totalvalue, player);
        totalvalue = countStableSequence(location, 0, -1, totalvalue, player);
        totalvalue = countStableSequence(location, 1, -1, totalvalue, player);
        totalvalue = countStableSequence(location, 1, 0, totalvalue, player);
        if (totalvalue >= 4) {
            return true;
        }
        totalvalue = countStableSequence(location, 1, 1, totalvalue, player);
        if (totalvalue >= 4) {
            return true;
        }
        totalvalue = countStableSequence(location, 0, 1, totalvalue, player);
        if (totalvalue >= 4) {
            return true;
        }
        totalvalue = countStableSequence(location, -1, 1, totalvalue, player);
        if (totalvalue >= 4) {
            return true;
        }
        totalvalue = countStableSequence(location, -1, 0, totalvalue, player);
        if (totalvalue >= 4) {
            return true;
        }
        totalvalue = countStableSequence(location, -1, -1, totalvalue, player);
        if (totalvalue >= 4) {
            return true;
        }
        totalvalue = countStableSequence(location, 0, -1, totalvalue, player);
        if (totalvalue >= 4) {
            return true;
        }
        totalvalue = countStableSequence(location, 1, -1, totalvalue, player);
        if (totalvalue >= 4) {
            return true;
        }
        return false;
    }


    int countStableSequence(Point location, int xOffset, int yOffset, int totalvalue, int player) {
        Point testLocation = new Point(location.x + xOffset, location.y + yOffset);

        if (isStable(testLocation, player)) {
            return totalvalue++;
        } else {
            return 0;
        }
    }

    boolean isStable(Point location, int player) {
        if (player == PLAYER_ONE) {
            return stablePieceArrayPlayerOne[location.x][location.y] == true;
        } else {
            return stablePieceArrayPlayerTwo[location.x][location.y] == true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public int getScore(Board board, int side, Point move) {
        return determineIfStable(move, side) ? 1 : 0;
    }
}
