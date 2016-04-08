package othello;
import othello.gui.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by thijs on 3-4-2016.
 */
public class Game extends AbstractModel {
    private final Board board;
    private int setSide;
    private int setLocation;

    private ArrayList<Move> moves = new ArrayList<>();

    class Move {
        public final int player;
        public final int location;

        public Move(int player, int location) {
            this.player = player;
            this.location = location;
        }

        @Override
        public String toString() {
            return "player: " + player +
                    "Location" + location + "\n";
        }
    }

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
            currentPlayer = 3 - currentPlayer;
        }

        int playerToInt(String player) {
            if (player.equals(STARTING_PLAYER)) {
                return 1;
            } else if (player.equals(SECOND_PLAYER)) {
                return 2;
            } else {
                return -1;
            }
        }

        String playerToString(int nr) {
            switch (nr) {
                case 1:
                    return STARTING_PLAYER;
                case 2:
                    return SECOND_PLAYER;
                default:
                    return "ERROR";
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
            if (clientNr == -1) {
                throw new IllegalStateException("CLIENT NOT SET YET!");
            }
            return currentPlayer == clientNr;
        }

        void setClientStarts(boolean clientStarts) {
            if (clientStarts) {
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
    	System.out.println("Othello doMove -> current player = " + playerState.currentPlayer);
    	System.out.println("Othello doMove -> my turn  = " + isClientsTurn());
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
        return (board.getPossibleMoves(1).length == 0 && board.getPossibleMoves(2).length == 0);
    }

    public int getScore(String player) {
        return board.getOccurrences(playerState.playerToInt(player));
    }


    public boolean isValidMove(Point location) {
        return board.isValidMove(location, playerState.currentPlayer);
    }

    public void setClientBegins(boolean clientBegins) {
        playerState.setClientStarts(clientBegins);
        if (clientBegins) {
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

    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
        int[][] test = new int[3][3];
        System.out.println(test);
    }

    public void fire(ActionEvent event) {
        listeners.forEach(listener -> listener.actionPerformed(event));
    }

    /**
     * Converts the location from a {@code int} into a point
     *
     * @param location the location to convert
     * @return the converted location
     */
    Point intToPoint(int location) {
        return new Point(location / board.getSize(), location % board.getSize());
    }

    /**
     * Converts the location from a {@code Point} into an int
     *
     * @param point the location to convert
     * @return the converted location
     */
    private int pointToInt(Point point) {
        return point.x * board.getSize() + point.y;
    }


    public void endTurn() {
        playerState.togglePlayer();
        if (board.getPossibleMoves(playerState.currentPlayer).length == 0) {
            playerState.togglePlayer();
            if (board.getPossibleMoves(playerState.currentPlayer).length == 0) {
                //game finished
            }
        }
        fire(new ActionEvent(this, AbstractModel.TURN_END, "COMPUTER_TURN"));

    }

    public void turnStart() {
        if (playerState.isClientTurn()) {
            doClientTurn();
        } else {
            doOpponentTurn();
        }
    }

    private void doOpponentTurn() {
        System.out.println("It's the turn of the opponent");
    }

    public String getCurrentPlayer() {
        return playerState.playerToString(playerState.currentPlayer);
    }

    public void prepareStandardGame() {
        //@// TODO: 5-4-2016 change
//        board.prepareTestGame();
        System.out.println("PREPARE GAME");
        board.prepareStandardGame(clientStart);
        fireEvents();
    }

    public void piecePlaced(Point location, int player) {
        moves.add(new Move(player, pointToInt(location)));
    }

    public void fireEvents() {
        System.out.println(moves);

        Arrays.stream(clearMoves())
                .peek(move -> setSide = move.player)
                .peek(move -> setLocation = move.location)
                .forEach(move -> fire(new ActionEvent(this, AbstractModel.PLACE_PIECE, "Place Piece")));
    }

    public Move[] clearMoves() {
        Move[] moveList = moves.toArray(new Move[0]);
        moves.clear();
        return moveList;
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
    
    public Board getBoard() {
    	return this.board;
    }
}