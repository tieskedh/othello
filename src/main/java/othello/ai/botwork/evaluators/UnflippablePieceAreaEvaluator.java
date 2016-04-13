package othello.ai.botwork.evaluators;

import othello.Board;
import othello.utility.Move;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.stream.IntStream;


/**
 * The Class UnflippablePieceAreaEvaluator.
 */
public class UnflippablePieceAreaEvaluator implements ActionListener, MaxDepthEvaluator {
    private static final int PLAYER_ONE = 1;
    private static final int PLAYER_TWO = 2;
    private boolean[][] stablePieceArrayPlayerOne = new boolean[10][10];
    private boolean[][] stablePieceArrayPlayerTwo = new boolean[10][10];

    /**
     * Instantiates a new unflippable piece area evaluator.
     *
     * @param board the board
     */
    public UnflippablePieceAreaEvaluator(Board board) {
        board.addActionListener(this);

        for(int i= 0; i  < 10; i++) {
            for(int j = 0; j < 10; j++) {
                if(i == 0 || i == 9 || j == 0 || j == 9) {
                    stablePieceArrayPlayerOne[i][j] = true;
                    stablePieceArrayPlayerTwo[i][j] = true;
                }
            }
        }
    }

    /**
     * Sets the stable.
     *
     * @param location the location
     * @param player the player
     */
    private void setStable(Point location, int player)  {
        if(player==1) {
            stablePieceArrayPlayerOne[location.x+1][location.y+1] = true;
        } else {
            stablePieceArrayPlayerTwo[location.x+1][location.y+1] = true;
        }

        IntStream.range(0, 9)
                .filter(nr->nr!=4)
                .filter(nr -> countStableSequence(location, nr/3-1, nr%3-1, 1, player)>=4)
                .mapToObj(nr -> new Point(location.x+nr/3-1, location.y+nr%3-1))
                .filter(point -> determineIfStable(point, player))
                .forEach(point -> setStable(point, player));
    }


    /**
     * Determine if stable.
     *
     * @param location the location
     * @param player the player
     * @return true, if successful
     */
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


    /**
     * Count stable sequence.
     *
     * @param location the location
     * @param xOffset the x offset
     * @param yOffset the y offset
     * @param totalvalue the totalvalue
     * @param player the player
     * @return the int
     */
    int countStableSequence(Point location, int xOffset, int yOffset, int totalvalue, int player) {
        Point testLocation = new Point(location.x + xOffset, location.y + yOffset);

        if (isStable(testLocation, player)) {
            return ++totalvalue;
        } else {
            return 0;
        }
    }

    /**
     * Checks if is stable.
     *
     * @param location the location
     * @param player the player
     * @return true, if is stable
     */
    boolean isStable(Point location, int player) {
        if (player == PLAYER_ONE) {
            return stablePieceArrayPlayerOne[location.x+1][location.y+1];
        } else {
            return stablePieceArrayPlayerTwo[location.x+1][location.y+1];
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Board board = ((Board)e.getSource());
        Move move = board.getLastMove();
        if(determineIfStable(move.getPoint(), move.player)) {
            setStable(move.getPoint(), move.player);
        }
    }

    /* (non-Javadoc)
     * @see othello.ai.botwork.evaluators.Evaluator#getScore(othello.Board, int, java.awt.Point)
     */
    @Override
    public int getScore(Board board, int side, Point move) {

        System.out.println("player one");
        System.out.println(Arrays.deepToString(stablePieceArrayPlayerOne));
        System.out.println("player two");
        System.out.println(Arrays.deepToString(stablePieceArrayPlayerTwo));

        int score = determineIfStable(move, side) ? 1 : 0;
        int opponentScore = determineIfStable(move, side-1)?1:0;
        System.out.println(score+"for player "+side+" with move "+move);
        return score-opponentScore*4;
    }

    /* (non-Javadoc)
     * @see othello.ai.botwork.evaluators.MaxDepthEvaluator#getMaxDepth()
     */
    @Override
    public int getMaxDepth() {
        return 1;
    }
}
