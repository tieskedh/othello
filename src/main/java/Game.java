import gui.AbstractModel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.IntStream;

/**
 * Created by thijs on 3-4-2016.
 */
public class Game extends AbstractModel{
    private final String PLAYER_ONE_STRING;
    private final String PLAYER_TWO_STRING;
    private final Board board;
    private int currentPlayer;
    private int setLocation;

    private LinkedList<ActionListener> actionListeners;

    public Game(Board board, String playerOne, String playerTwo) {
        PLAYER_ONE_STRING = playerOne;
        PLAYER_TWO_STRING = playerTwo;
        this.board = board;
        actionListeners = new LinkedList<>();
    }

    public boolean doMove(Point location) {
        return board.doMove(location, currentPlayer);
//            throw new IllegalStateException("False move. Not allowed.");
    }

    public boolean doMove(int location) {
        Point locationPoint = intToPoint(location);
        return board.doMove(locationPoint, currentPlayer);
    }

    public boolean checkIfMatchDone() {
        return (board.getPossibleMoves(1).length == 0 && board.getPossibleMoves(2).length==0);
    }

    public int getScore(String player) {
        return board.getOccurrences(playerToInt(player));
    }

    public Board getBoard() {
        return board;
    }

    public boolean isValidMove(Point location) {
        return board.isValidMove(location, currentPlayer);
    }

    public boolean isClientsTurn() {
        return currentPlayer==1;
    }

    public void setClientBegins(boolean clientBegins) {
        currentPlayer = (clientBegins)? 1: 2;
    }

    @Override
    public int[] getValidSets() {
        return Arrays.stream(board.getPossibleMoves(currentPlayer))
        .mapToInt(this::pointToInt)
        .toArray();
    }

    @Override
    public int getSetLocation() {
        return 0;
    }

    @Override
    public int getSide() {
        return currentPlayer;
    }

    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    private String playerToString(int playerNr) {
        switch (playerNr) {
            case 1: return PLAYER_ONE_STRING;
            case 2: return PLAYER_TWO_STRING;
            default: return "UNKNOWN";
        }
    }
    private int playerToInt(String player) {
        if(player.equals(PLAYER_ONE_STRING)) return 1;
        if(player.equals(PLAYER_TWO_STRING)) return 2;
        return -1;
    }

    /**
     * Converts the location from a {@code int} into a point
     * @param location the location to convert
     * @return the converted location
     */
    private Point intToPoint(int location) {
        return new Point(location/board.getSize(), location%board.getSize());
    }

    /**
     * Converts the location from a {@code Point} into an int
     * @param point the location to convert
     * @return the converted location
     */
    private int pointToInt(Point point) {
        return point.x * board.getSize()+ point.y;
    }


    public void endTurn() {
        currentPlayer = 1-currentPlayer;
    }

    public String getCurrentPlayer() {
        return playerToString(currentPlayer);
    }

    public String getNextPlayer() {
        return playerToString(1-currentPlayer);
    }

}
