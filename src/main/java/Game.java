import gui.AbstractModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by thijs on 3-4-2016.
 */
public class Game extends AbstractModel{
    private final String PLAYER_ONE_STRING;
    private final String PLAYER_TWO_STRING;
    private final Board board;
    private int currentPlayer;
    private int setSide;
    private int setLocation;

    private LinkedList<ActionListener> listeners = new LinkedList<>();

    public Game(int boardSize, String playerOne, String playerTwo) {
        PLAYER_ONE_STRING = playerOne;
        PLAYER_TWO_STRING = playerTwo;
        this.board = new Board(boardSize, this);
    }

    private void doMove(Point location) {

        System.out.println("doMove player: "+currentPlayer);
        if (board.doMove(location, currentPlayer)) {
            setSide = currentPlayer;
            endTurn();
        } else {
            throw new IllegalStateException("False move. Not allowed.");
        }
    }

    public void doMove(int location) {
        Point locationPoint = intToPoint(location);
        doMove(locationPoint);
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
        if(currentPlayer==1) {
            setClientTurn();
        } else {
            setOponentTurn();
        }
    }

    @Override
    public int[] getValidSets() {
        return Arrays.stream(board.getPossibleMoves(currentPlayer))
                .mapToInt(this::pointToInt)
                .toArray();
    }

    private void setClientTurn() {
        fire(new ActionEvent(this, AbstractModel.TURN_START, "CLIENT IS ON SET"));
    }

    @Override
    public int getSetLocation() {
        return setLocation;
    }

    @Override
    public int getSide() {
        return setSide;
    }

    public void addActionListener(ActionListener listener) { listeners.add(listener);}

    public void fire(ActionEvent event) {
        listeners.forEach(listener -> listener.actionPerformed(event));
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
        currentPlayer = 3-currentPlayer;
        if(board.getPossibleMoves(currentPlayer).length==0) {
            currentPlayer = 3-currentPlayer;
        }
        if(board.getPossibleMoves(currentPlayer).length==0) {
            //game finished
        } else {
            if(currentPlayer==1) {
                setClientTurn();
            } else {
                setOponentTurn();
            }
        }
    }

    private void setOponentTurn() {
        currentPlayer = 2;
        fire(new ActionEvent(this, AbstractModel.TURN_END, "COMPUTER_TURN"));
    }

    public String getCurrentPlayer() {
        return playerToString(currentPlayer);
    }

    public String getNextPlayer() {
        return playerToString(1-currentPlayer);
    }

    public void prepareStandardGame() {
        board.prepareStandardGame();
        fire(new ActionEvent(this, AbstractModel.TURN_START, "GAME STARTED"));

    }

    public void piecePlaced(Point location, int player) {
        setLocation = pointToInt(location);
        setSide = player;
        fire(new ActionEvent(this, AbstractModel.PLACE_PIECE, "PIECE PLACED"));
        endTurn();
    }
}
