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
    private final Board board;
    private int setSide;
    private int setLocation;

    private LinkedList<ActionListener> listeners = new LinkedList<>();
    private boolean clientStart;

    private Players playerState;
    public boolean isClientsTurn() {
        return playerState.currentPlayer == playerState.playerToInt(playerState.CLIENT);
    }

    private class Players {
        private final String STARTING_PLAYER;
        private final String SECOND_PLAYER;
        private String CLIENT;
        private String OPONENT;
        private int currentPlayer = 1;

        public Players(String playerOne, String playerTwo) {
            STARTING_PLAYER = playerOne;
            SECOND_PLAYER = playerTwo;
        }

        public void togglePlayer() {
            currentPlayer = 3-currentPlayer;
        }
        int playerToInt(String player) {
            if(player.equals(STARTING_PLAYER)) {
                return 1;
            } else if(player.equals(SECOND_PLAYER)) {
                return 2;
            } else {
                return -1;
            }
        }

        String playerToString(int nr) {
            switch (nr) {
                case 1: return STARTING_PLAYER;
                case 2:return SECOND_PLAYER;
                default: return "ERROR";
            }
        }

        boolean isClient(String player) {
            return player.equals(CLIENT);
        }

        public int getCurrentPlayer() {
            return currentPlayer;
        }

        boolean isClientTurn() {
            /**
             * @// TODO: 5-4-2016 remove
             */
            int clientNr = playerToInt(CLIENT);
            if(clientNr==-1) {
                throw new IllegalStateException("CLIENT NOT SET YET!");
            }
            return currentPlayer== clientNr;
        }

        void setClientStarts(boolean clientStarts) {
            if(clientStarts) {
                CLIENT = STARTING_PLAYER;
                OPONENT = SECOND_PLAYER;
            } else {
                CLIENT = SECOND_PLAYER;
                OPONENT = STARTING_PLAYER;
            }
        }
    }





    public Game(int boardSize, String playerOne, String playerTwo) {
        playerState = new Players(playerOne, playerTwo);
        this.board = new Board(boardSize, this);
    }

    private void doMove(Point location) {
        if (board.doMove(location, playerState.currentPlayer)) {
            System.out.println("doMove player: " + playerState.currentPlayer + "did move " + location);
            setSide = playerState.currentPlayer;
        } else {
            System.out.println("doMove player: " + playerState.currentPlayer + "could not do move " + location);
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
        return board.getOccurrences(playerState.playerToInt(player));
    }


    public boolean isValidMove(Point location) {
        return board.isValidMove(location, playerState.currentPlayer);
    }

    public void setClientBegins(boolean clientBegins) {
        playerState.setClientStarts(clientBegins);
        if(clientBegins) {
            System.out.println("Client should begin");
            doClientTurn();
        } else {
            System.out.println("Oponent should begin");
            doOpponentTurn();
        }
    }

    @Override
    public int[] getValidSets() {
        return Arrays.stream(board.getPossibleMoves(playerState.currentPlayer))
                .mapToInt(this::pointToInt)
                .toArray();
    }

    private void doClientTurn() {
        System.out.println(this);
        System.out.println("Client can perform move");
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
        System.out.println("turn ended");
        playerState.togglePlayer();
        if(board.getPossibleMoves(playerState.currentPlayer).length==0) {
            System.out.println("No possible moves for " + playerState.currentPlayer);
            playerState.togglePlayer();
            if (board.getPossibleMoves(playerState.currentPlayer).length == 0) {
                //game finished
            }
        }
        if(playerState.isClientTurn()) {
            doClientTurn();
        } else {
            doOpponentTurn();
        }
    }

    private void doOpponentTurn() {
        System.out.println("It's the turn of the opponent");
        fire(new ActionEvent(this, AbstractModel.TURN_END, "COMPUTER_TURN"));
    }

    public String getCurrentPlayer() {
        return playerState.playerToString(playerState.currentPlayer);
    }

    public void prepareStandardGame() {
        //@// TODO: 5-4-2016 change
//        board.prepareTestGame();
        board.prepareStandardGame(clientStart);
    }

    public void piecePlaced(Point location, int player) {
        setLocation = pointToInt(location);
        setSide = player;
        fire(new ActionEvent(this, AbstractModel.PLACE_PIECE, "PIECE PLACED"));
    }

    public int getClient() {
        return playerState.playerToInt(playerState.CLIENT);
    }

    public int getOpponent() {
        return playerState.playerToInt(playerState.OPONENT);
    }

    @Override
    public String toString() {
        return board.toString();
    }
}