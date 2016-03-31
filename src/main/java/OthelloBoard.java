import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import gui.AbstractModel;

public class OthelloBoard extends AbstractModel {
    public static final int EMPTY = 0;
    private int boardSize;
    private int board[][];

    private String playerOne;
    private String playerTwo;
    private String currentPlayer;
    private int setLocation;

    public ActionListener view;
    private LinkedList<ActionListener> actionListeners = new LinkedList<>();

    //the digits on the internal board.
    private static final int PLAYER_1 = 1;
    private static final int PLAYER_2 = 2;
    private int side;

    public OthelloBoard(int boardSize, String playerOne, String playerTwo) {
        this.boardSize = boardSize;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        board = new int[boardSize][boardSize];
    }

    public int[][] getBoard() {
        return board;
    }

    public Point[] getPossibleMoves(String player) {
        ArrayList<Point> moveList = new ArrayList<Point>();
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                Point location = new Point(x, y);
                if (validateMove(location, player)) {
                    moveList.add(location);
                }
            }
        }

        return moveList.toArray(new Point[0]);
    }

    public int getBoardSize() {
        return boardSize;
    }

    /*
    executes one move from the given player
     */
    public void doMove(Point location, String player) {
        System.out.println("Do Move board entered");
        if (validateMove(location, player)) {
            placePiece(location, player);
            ArrayList<Point> toFlip = new ArrayList<Point>();
            //assemble list from each direction and add.
            ArrayList<Point> inputArray;
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int yOffset = -1; yOffset <= 1; yOffset++) {
                    inputArray = checkLinePieces(location, xOffset, yOffset, player, toFlip);

                    if (inputArray != null) {
                        toFlip.addAll(inputArray);
                    }
                }
            }
//            toFlip.addAll(checkLinePieces(location, 1, 0, player, toFlip));
//            toFlip.addAll(checkLinePieces(location, 0, 1, player, toFlip));
//            toFlip.addAll(checkLinePieces(location, -1, 0, player, toFlip));
//            toFlip.addAll(checkLinePieces(location, 0, -1, player, toFlip));
//            toFlip.addAll(checkLinePieces(location, 1, 1, player, toFlip));
//            toFlip.addAll(checkLinePieces(location, 1, -1, player, toFlip));
//            toFlip.addAll(checkLinePieces(location, -1, 1, player, toFlip));
//            toFlip.addAll(checkLinePieces(location, -1, -1, player, toFlip));
            System.out.println(toFlip);
            toFlip.stream()
                    .distinct()
                    .forEach(this::flipPiece);

            System.out.println(Arrays.deepToString(board));
        } else {
            throw new IllegalStateException("False move. Not allowed.");
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
        fireEvent(new ActionEvent(this, AbstractModel.PLACE_PIECE, "Flip Piece"));

    }

    /*
     Flips the piece at the given coordinates.
     */
    private void placePiece(Point location, String player) throws IllegalStateException {
        if (player.equals(playerOne)) {
            side = PLAYER_1;
        } else {
            side = PLAYER_2;
        }
        board[location.x][location.y] = side;
        setSetLocation(toInt(location));
        System.out.println("fire event");
        fireEvent(new ActionEvent(this, AbstractModel.PLACE_PIECE, "Place Piece"));

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

        for (int[] row : board) {
            for (int fieldVal : row) {
                if (fieldVal == comparable) {
                    total++;
                }
            }
        }
        return total;
    }

    public String getWinner() {
        int player1 = getScore(playerOne);
        int player2 = getScore(playerTwo);
        if (player1 > player2) {
            return playerOne;
        }
        if (player2 > player1) {
            return playerTwo;
        }
        return null;

    }

    public void clearBoard() {
        for (int[] row : board) {
            for (int point : row) {
                point = 0;
            }
        }
    }

    public void prepareStandardGame() {
        placePiece(new Point(1, 1), playerOne);
        placePiece(new Point(2, 1), playerTwo);
        placePiece(new Point(3, 1), playerTwo);

        placePiece(new Point(1, 2), playerTwo);
        placePiece(new Point(2, 2), playerOne);
    }

    private boolean validateMove(Point location, String player) {
        int playerNr = playerStringToInt(player);
        if (board[location.x][location.y] == 0) {
            if ((checkLineValidation(location, 1, 0, player) && board[location.x + 1][location.y] != playerNr) ||
                    (checkLineValidation(location, 0, 1, player) && board[location.x][location.y + 1] != playerNr) ||
                    (checkLineValidation(location, -1, 0, player) && board[location.x - 1][location.y] != playerNr) ||
                    (checkLineValidation(location, 0, -1, player) && board[location.x][location.y - 1] != playerNr) ||
                    (checkLineValidation(location, 1, 1, player) && board[location.x + 1][location.y + 1] != playerNr) ||
                    (checkLineValidation(location, 1, -1, player) && board[location.x + 1][location.y - 1] != playerNr) ||
                    (checkLineValidation(location, -1, 1, player) && board[location.x - 1][location.y + 1] != playerNr) ||
                    (checkLineValidation(location, -1, -1, player) && board[location.x - 1][location.y - 1] != playerNr)
                    ) {
                return true;
            }
        }
        return false;
    }

    private void dumpboard() {
        for (int[] row : board
                ) {
            for (int position : row
                    ) {
                System.out.println(position);

            }
            System.out.println("\n");
        }
    }

    /**
     * first <= second <= third
     */
    private boolean PointOutOfBounds(Point location) {
        return location.x < 0 || location.x > boardSize - 1 || location.y < 0 || location.y > boardSize - 1;
    }

    private boolean checkLineValidation(Point location, int offsetX, int offsetY, String player) {
        int playerNr = playerStringToInt(player);
        Point testPoint = new Point(location);
        testPoint.move(testPoint.x + offsetX, testPoint.y + offsetY);
        if (PointOutOfBounds(testPoint) || board[testPoint.x][testPoint.y] == EMPTY) {
            return false;
        } else if (board[testPoint.x][testPoint.y] == playerNr) {
            return true;
        }
        return checkLineValidation(testPoint, offsetX, offsetY, player);
    }


    private ArrayList<Point> checkLinePieces(Point location, int offsetX, int offsetY, String player, ArrayList<Point> piecesList) {
        int playerNr = playerStringToInt(player);
        Point testPoint = new Point(location);

        testPoint.move(testPoint.x + offsetX, testPoint.y + offsetY);

        //If not piece or out of bounds, end empty
        if (PointOutOfBounds(testPoint) || board[testPoint.x][testPoint.y] == 0) {
            return null;
        }
        //If it encounters another piece of the same player, end.
        if (board[testPoint.x][testPoint.y] == playerNr) {
            return piecesList;
        } else {
            //if it encounters a piece of the opposite player, add to fliplist.
            piecesList.add(testPoint);
            //get new location and go recursive
            return checkLinePieces(testPoint, offsetX, offsetY, player, piecesList);
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

    private int toInt(Point point) {
        return point.x * boardSize + point.y;
    }

    @Override
    public int[] getValidSets() {
        Point[] validPoints = getPossibleMoves(currentPlayer);
        int[] toReturn = new int[validPoints.length];
        int i = 0;
        for (Point validPoint : validPoints) {
            toReturn[i++] = toInt(validPoint);
        }
        return toReturn;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    @Override
    public int getSetLocation() {
        return setLocation;
    }


    @Override
    public int getSide() {
        return side;
    }

    public void setSetLocation(int setLocation) {
        this.setLocation = setLocation;
    }

    public void fireEvent(ActionEvent event) {
        for (ActionListener actionListener : actionListeners) {
            actionListener.actionPerformed(event);
        }
    }

    public void addActionListener(ActionListener actionlistener) {
        actionListeners.add(actionlistener);
    }

    public void turnStart() {
        fireEvent(new ActionEvent(this, AbstractModel.TURN_START, "Begin Turn"));

    }

    public void turnEnd() {
        fireEvent(new ActionEvent(this, AbstractModel.TURN_END, "End Turn"));
    }
}
