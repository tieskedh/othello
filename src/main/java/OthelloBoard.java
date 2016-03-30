import javax.swing.text.Position;
import java.awt.*;
import java.util.ArrayList;


public class OthelloBoard {
    private int boardSize;
    private int board[][];

    private String playerOne;
    private String playerTwo;

    //the digits on the internal board.
    private static final int PLAYER_1 = 1;
    private static final int PLAYER_2 = 2;

    public OthelloBoard(int boardSize) {
        this.boardSize = boardSize;
    }

    public int[][] getBoard() {
        return board;
    }

    public Point[] getPossibleMoves(String player) {
        ArrayList<Point> moveList = new ArrayList<Point>();
        int x = 0;
        int y = 0;
        for (int[] column : board) {
            x++;
            y = 0;
            for (int row : column) {
                y++;
                Point location = new Point(x, y);
                if (validateMove(location, player)) {
                    moveList.add(location);
                }
            }
        }

        return (Point[]) moveList.toArray();
    }

    public int getBoardSize() {
        return boardSize;
    }


    /*
    executes one move from the given player
     */
    public void doMove(Point location, String player) {
        if (validateMove(location, player)) {
            placePiece(location, player);
            ArrayList<Point> toFlip = new ArrayList<Point>();
            //assemble list from each direction and add.
            toFlip.addAll(checkLinePieces(location, 1, 0, player, toFlip));
            toFlip.addAll(checkLinePieces(location, 0, 1, player, toFlip));
            toFlip.addAll(checkLinePieces(location, -1, 0, player, toFlip));
            toFlip.addAll(checkLinePieces(location, 0, -1, player, toFlip));
            toFlip.addAll(checkLinePieces(location, 1, 1, player, toFlip));
            toFlip.addAll(checkLinePieces(location, 1, -1, player, toFlip));
            toFlip.addAll(checkLinePieces(location, -1, 1, player, toFlip));
            toFlip.addAll(checkLinePieces(location, -1, -1, player, toFlip));

            for (Point flipPoint : toFlip) {
                flipPiece(flipPoint);
            }
        } else {
            throw new IllegalStateException("False move.");
        }
    }

    /*
    places a piece at the given coordinates belonging to the given player.
     */
    private void placePiece(Point location, String player) throws IllegalStateException {
        if (player == playerOne) {
            board[location.x][location.y] = PLAYER_1;
        } else {
            board[location.x][location.y] = PLAYER_2;
        }

    }

    /*
    flips the owner of the piece at the given coordinates.
     */
    private void flipPiece(Point location) {
        if (board[location.x][location.y] == PLAYER_1) {
            board[location.x][location.y] = PLAYER_2;
        } else if (board[location.x][location.y] == PLAYER_2) {
            board[location.x][location.y] = PLAYER_1;
        }
    }

    public boolean checkIfMatchDone() {
        if (getPossibleMoves(playerOne) == null && getPossibleMoves(playerTwo) == null) {
            return true;
        }
        return false;
    }

    public int getScore(String player) {
        int total = 0;
        int comparable = playerStringToInt(player);

        for (int[] column : board) {
            for (int point : column) {
                if (point == comparable) {
                    total++;
                }
            }
        }
        return total;
    }

    public int getWinner() {
        int player1 = getScore(playerOne);
        int player2 = getScore(playerTwo);
        if (player1 > player2) {
            return 1;
        }
        if (player2 > player1) {
            return 2;
        }
        return 0;

    }

    public void clearBoard() {
        for (int[] column : board) {
            for (int point : column) {
                point = 0;
            }
        }
    }

    public void prepareStandardGame() {
        placePiece(new Point(3, 3), playerOne);
        placePiece(new Point(4, 3), playerTwo);
        placePiece(new Point(4, 4), playerOne);
        placePiece(new Point(3, 4), playerTwo);
    }

    private boolean validateMove(Point location, String player) {
        int playerNr = playerStringToInt(player);
        if (board[location.x][location.y] == 0 &&
                ((board[location.x + 1][location.y] != playerNr && checkLineValidation(location, 1, 0, player)) ||
                        (board[location.x][location.y + 1] != playerNr && checkLineValidation(location, 0, 1, player)) ||
                        (board[location.x - 1][location.y] != playerNr && checkLineValidation(location, -1, 0, player)) ||
                        (board[location.x][location.y - 1] != playerNr && checkLineValidation(location, 0, -1, player)) ||
                        (board[location.x + 1][location.y + 1] != playerNr && checkLineValidation(location, 1, 1, player)) ||
                        (board[location.x + 1][location.y - 1] != playerNr && checkLineValidation(location, 1, -1, player)) ||
                        (board[location.x - 1][location.y + 1] != playerNr && checkLineValidation(location, -1, 1, player)) ||
                        (board[location.x - 1][location.y - 1] != playerNr && checkLineValidation(location, -1, -1, player))
                )
                ) {
            return true;
        }
        return false;
    }

    private boolean checkLineValidation(Point location, int offsetx, int offsety, String player) {
        int playerNr = playerStringToInt(player);

        if (location.x < 0 || location.x > boardSize - 1 || location.y < 0 || location.y > boardSize - 1 || board[location.x][location.y] == 0) {
            return false;
        } else if (board[location.x][location.y] == playerNr) {
            return true;
        }
        location.move(location.x + offsetx, location.y + offsety);
        return checkLineValidation(location, offsetx, offsety, player);
    }


    private ArrayList<Point> checkLinePieces(Point location, int offsetX, int offsetY, String player, ArrayList<Point> piecesList) {
        int playerNr = playerStringToInt(player);

        //If not piece or out of bounds, end empty
        if (location.x < 0 || location.x > boardSize - 1 || location.y < 0 || location.y > boardSize - 1 || board[location.x][location.y] == 0) {
            return null;
        }
        //If it encounters another piece of the same player, end.
        if (board[location.x][location.y] == playerNr) {
            return piecesList;
        } else {
            //if it encounters a piece of the opposite player, add to fliplist.
            piecesList.add(location);
            //get new location and go recursive
            location.move(location.x + offsetX, location.y + offsetY);
            return checkLinePieces(location, offsetX, offsetY, player, piecesList);
        }
    }

    public void setPlayerOne(String player) {
        playerOne = player;
    }

    public void setPlayerTwo(String player) {
        playerTwo = player;
    }

    //Replaces player name with player number
    private int playerStringToInt(String player) {
        if (player == playerOne) {
            return PLAYER_1;
        } else {
            return PLAYER_2;
        }

    }
}
