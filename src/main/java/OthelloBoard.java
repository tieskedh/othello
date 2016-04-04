import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.IntStream;

import gui.AbstractModel;

public class OthelloBoard extends AbstractModel {
    private static final int EMPTY = 0;
    private final int BOARD_SIZE;
    private int board[][];

    private final String PLAYER_ONE_STRING;
    private final String PLAYER_TWO_STRING;
    private int currentPlayer;
    private int setLocation;

    private LinkedList<ActionListener> actionListeners = new LinkedList<>();

    //the digits on the internal board.
    private static final int PLAYER_1 = 1;
    private static final int PLAYER_2 = 2;
    private int side;

    public OthelloBoard(int boardSize, String playerOne, String playerTwo) {
        this.BOARD_SIZE = boardSize;
        this.PLAYER_ONE_STRING = playerOne;
        this.PLAYER_TWO_STRING = playerTwo;
        board = new int[boardSize][boardSize];
    }

    public int[][] getBoard() {
        return board;
    }


    /*
    executes one move from the given player
     */
    public void doMove(Point location, String player) {
        System.out.println(location);
        int playerNr = playerStringToInt(player);
        if (isValidMove(location, playerNr)) {
            //assemble list from each direction and add.

            IntStream.range(0, 8)
                    .mapToObj(nr->checkLinePieces(location, nr/3-1,nr%3-1,playerNr, new ArrayList<>()))
                    .flatMap(Collection::stream)
                    .distinct()
                    .forEach(this::flipPiece);
            System.out.println(Arrays.deepToString(board));
        } else {
            throw new IllegalStateException("False move. Not allowed.");
        }
    }

    private int getAtLocation(Point location) {
        return board[location.x][location.y];
    }
    private void setAtLocation(Point location,int player) {
        side = player;
        board[location.x][location.y] = player;
        setSetLocation(toInt(location));
        System.out.println("fire event");
        fireEvent(new ActionEvent(this, AbstractModel.PLACE_PIECE, "Place Piece"));
    }

    /*
    flips the owner of the piece at the given coordinates.
     */
    private void flipPiece(Point location) {
        if (getAtLocation(location)==PLAYER_1) {
            setAtLocation(location, PLAYER_2);
        } else if (getAtLocation(location) == PLAYER_2) {
            setAtLocation(location, PLAYER_1);
        }
    }


    public boolean checkIfMatchDone() {
        return (getPossibleMoves(PLAYER_1).length == 0 && getPossibleMoves(PLAYER_2).length == 0);
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

    public void clearBoard() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
    }

    public void prepareStandardGame() {
        setAtLocation(new Point(3, 3), PLAYER_1);
        setAtLocation(new Point(4, 3), PLAYER_2);
        setAtLocation(new Point(3, 4), PLAYER_2);
        setAtLocation(new Point(4, 4), PLAYER_1);
    }

    public void prepareTestGame() {
        setAtLocation(new Point(1, 1), PLAYER_1);
        setAtLocation(new Point(2, 1), PLAYER_2);
        setAtLocation(new Point(1, 2), PLAYER_2);
        setAtLocation(new Point(2, 2), PLAYER_1);
    }

    private boolean isValidMove(Point location, int player) {
        return IntStream.range(0, 8)
                .mapToObj(nr->checkLinePieces(location, nr/3-1,nr%3-1,player, new ArrayList<>()))
                .noneMatch(ArrayList::isEmpty);
    }



    private boolean PointOutOfBounds(Point location) {
        return location.x < 0 || location.x > BOARD_SIZE - 1 || location.y < 0 || location.y > BOARD_SIZE - 1;
    }

    private ArrayList<Point> checkLinePieces(Point location, int offsetX, int offsetY, int player, ArrayList<Point> piecesList) {
        Point testPoint = new Point(location);

        testPoint.move(testPoint.x + offsetX, testPoint.y + offsetY);

        //If not piece or out of bounds and empty
        if (PointOutOfBounds(testPoint) || board[testPoint.x][testPoint.y] == 0) {
            return new ArrayList<>();
        }
        //If it encounters another piece of the same player, end.
        if (board[testPoint.x][testPoint.y] == player) {
            return piecesList;
        } else {
            //if it encounters a piece of the opposite player, add to fliplist.
            piecesList.add(testPoint);
            //get new location and go recursive
            return checkLinePieces(testPoint, offsetX, offsetY, player, piecesList);
        }
    }

    //Replaces player name with player number
    private int playerStringToInt(String player) {
        if (player.equals(PLAYER_ONE_STRING)) {
            return PLAYER_1;
        } else {
            return PLAYER_2;
        }

    }

    private int toInt(Point point) {
        return point.x * BOARD_SIZE + point.y;
    }

    private int[] getPossibleMoves(int player) {

        int[] test= IntStream.range(0, BOARD_SIZE * BOARD_SIZE)
                .mapToObj(nr ->new Point(nr/ BOARD_SIZE, nr% BOARD_SIZE))
                .filter(location -> isValidMove(location,player))
                .mapToInt(this::toInt)
                .toArray();
        System.out.println(Arrays.toString(test));
        return test;
    }

    @Override
    public int[] getValidSets() {
        return getPossibleMoves(currentPlayer);
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = playerStringToInt(currentPlayer);
    }

    @Override
    public int getSetLocation() {
        return setLocation;
    }


    @Override
    public int getSide() {
        return side;
    }

    private void setSetLocation(int setLocation) {
        this.setLocation = setLocation;
    }

    private void fireEvent(ActionEvent event) {
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
