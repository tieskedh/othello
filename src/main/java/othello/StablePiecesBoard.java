package othello;

/**
 * Created by thijs on 11-4-2016.
 */
public class StablePiecesBoard extends Board{
    int[][] values;

    public StablePiecesBoard(int boardSize) {
        super(boardSize);
        values = new int[BOARD_SIZE][BOARD_SIZE];
    }

    public StablePiecesBoard(Board board) {
        super(board);
        values = new int[BOARD_SIZE][BOARD_SIZE];
    }


}
