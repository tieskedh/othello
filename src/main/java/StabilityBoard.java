import othello.Board;
import othello.Game;

import java.awt.*;

/**
 * Created by thijs on 11-4-2016.
 */
public class StabilityBoard extends Board{
    int[][] stability;

    public StabilityBoard(int boardSize) {
        super(boardSize);
        stability = new int[6][BOARD_SIZE];
    }

    public StabilityBoard(Game game, int[][] board) {
        super(game, board);
        stability = new int[6][BOARD_SIZE];
    }

    public StabilityBoard(StabilityBoard board) {
        super(board);
        stability = new int[6][BOARD_SIZE];
    }
    public StabilityBoard(Board board) {
        super(board);
        stability = new int[6][BOARD_SIZE];
    }

    private void progressBoard() {
        for(int direction = 0; direction < 2; direction++) {
            for(int index = 0; index < 8; index++) {
                stability[direction][index]=8;
            }
        }
        for (int direction = 2; direction < 6; direction++) {
            for(int index = 0; index < 8; index++) {
                stability[direction][index] = index;
            }
        }
    }

    @Override
    protected void setAtLocation(Point location, int player) {
        if(getAtLocation(location) != player) {
            super.setAtLocation(location, player);
            if(player==0) {
                stability[0][location.x]--;
                stability[1][location.y]--;
                int diagonalDownToUp =location.x;
            } else {

            }
        }
    }
}
