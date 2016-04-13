package othello;

import othello.gui.*;
import othello.utility.Move;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * The Game class is the primary part of the Model.
 * This contains all the information and the manipulation methods for playing the game.
 * It is extended from AbstractModel to give data to the GameView
 */
public class Game extends AbstractModel implements ActionListener {
    //contains the actual board and pieces to track internally.
    private Board board;
    private int setSide;
    private int setLocation;

    // Temporary storage for all changes to the board.
    private ArrayList<Move> moves = new ArrayList<>();

    private LinkedList<ActionListener> listeners = new LinkedList<>();

    // Contains the current state of the players
    private Players playerState;
    private boolean fireEvents;

    /**
     * Constructor
     *
     * @param playerOne
     * @param playerTwo
     */
    public Game(String playerOne, String playerTwo) {
        playerState = new Players(playerOne, playerTwo);
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source instanceof Board) {
            if(fireEvents) {
                Move lastMove = ((Board)source).getLastMove();
                setSide = lastMove.player;
                setLocation = lastMove.location;
                fire(new ActionEvent(this, AbstractModel.PLACE_PIECE, "PIECE PLACED"));
            }
        }
    }

    public void fireEvents(boolean fireEvents) {
        this.fireEvents = fireEvents;
    }


    /**
     * Internal class Players.
     * Contains information about both players.
     */
    private class Players {
        //The player names
        private String CLIENT;
        private String OPPONENT;
        //Which player goes first and which one second.
        private final String STARTING_PLAYER;
        private final String SECOND_PLAYER;
        //Which player currently has the turn.
        private int currentPlayer = 1;

        /**
         * Constructor
         *
         * @param playerOne
         * @param playerTwo
         */
        public Players(String playerOne, String playerTwo) {
            STARTING_PLAYER = playerOne;
            SECOND_PLAYER = playerTwo;
        }

        /**
         * Switches which player is the current player.
         */
        public void togglePlayer() {
            currentPlayer = 3 - currentPlayer;
        }

        /**
         * Converts player name to player number
         *
         * @param player
         * @return
         */
        public int playerToInt(String player) {
            if (player.equals(STARTING_PLAYER)) {
                return 1;
            } else if (player.equals(SECOND_PLAYER)) {
                return 2;
            } else {
                return -1;
            }
        }

        /**
         * Converts player number to player name
         */
        public String playerToString(int nr) {
            switch (nr) {
                case 1:
                    return STARTING_PLAYER;
                case 2:
                    return SECOND_PLAYER;
                default:
                    return "ERROR";
            }
        }

        //Checks if given player is the client
        public boolean isClient(String player) {
            return player.equals(CLIENT);
        }

        public int getCurrentPlayer() {
            return currentPlayer;
        }

        /**
         * Checks if client is current player
         */
        private boolean isClientTurn() {

            int clientNr = playerToInt(CLIENT);
            if (clientNr == -1) {
                throw new IllegalStateException("CLIENT NOT SET YET!");
            }
            return currentPlayer == clientNr;
        }


        /**
         * Sets whether the Client goes first or second
         *
         * @param clientStarts
         */
        void setClientStarts(boolean clientStarts) {
            if (clientStarts) {
                CLIENT = STARTING_PLAYER;
                OPPONENT = SECOND_PLAYER;
            } else {
                CLIENT = SECOND_PLAYER;
                OPPONENT = STARTING_PLAYER;
            }
        }
    }


    /**
     * Executes a move to the given Point on the Board
     * Checks for validity move.
     *
     * @param location
     * @throws IllegalStateException
     */
    private void doMove(Point location) throws IllegalStateException {
        if (board.doMove(location, playerState.currentPlayer)) {
            setSide = playerState.currentPlayer;
        } else {
            throw new IllegalStateException("False move. Not allowed.");
        }
    }

    /**
     * Additive to doMove(Point location)
     * Transforms Board location from integer (0-BOARD_LENGTH^2) to Point(x,y)
     *
     * @param location
     */
    public void doMove(int location) {
        doMove(intToPoint(location));
    }

    /**
     * Checks if move is valid.
     *
     * @param location
     * @return
     */
    public boolean isValidMove(Point location) {
        return board.isValidMove(location, playerState.currentPlayer);
    }

    /**
     * Sets if the Client has the first turn, then prepares it.
     *
     * @param clientBegins
     */
    public void setClientBegins(boolean clientBegins) {
        playerState.setClientStarts(clientBegins);
        if (clientBegins) {
            doClientTurn();
        }
    }


    public boolean isClientsTurn() {
        return playerState.isClientTurn();
    }

    /**
     * Enables buttons for Client
     */
    private void doClientTurn() {
        fire(new ActionEvent(this, AbstractModel.TURN_START, "CLIENT IS ON SET"));
    }


    /**
     * adds ActionListener to the list.
     *
     * @param listener
     */
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }

    /**
     * Fires event to all subscribed listeners.
     *
     * @param event
     */
    public void fire(ActionEvent event) {
        listeners.forEach(listener -> listener.actionPerformed(event));
    }


    /**
     * Converts the location from an Integer into a Point(x,y)
     *
     * @param location the location to convert
     * @return the converted location
     */
    public Point intToPoint(int location) {
        return new Point(location / board.getSize(), location % board.getSize());
    }

    /**
     * Converts the location from a Point(x,y) into an Integer
     *
     * @param point the location to convert
     * @return the converted location
     */
    public int pointToInt(Point point) {
        return point.x * board.getSize() + point.y;
    }

    /**
     * Ends the player's turn
     */
    public void endTurn() {
        //changes current player
        playerState.togglePlayer();
        //Checks for end of the game
        if (board.getPossibleMoves(playerState.currentPlayer).length == 0) {
            playerState.togglePlayer();
            if (board.getPossibleMoves(playerState.currentPlayer).length == 0) {
                //game finished
            }
        }
        //Informs Views
        fire(new ActionEvent(this, AbstractModel.TURN_END, "COMPUTER_TURN"));

    }

    /**
     * Starts the next turn
     */
    public void turnStart() {
        if (playerState.isClientTurn()) {
            doClientTurn();
        }
    }

    /**
     * Sets the Board up for a standarized games
     */
    public void prepareStandardGame() {
        fireEvents=true;
        board.prepareStandardGame();
        fireEvents=false;
        //informs the View of the new pieces
    }

    /**
     * returns all valid moves at this point for the current player
     * Inherited from AbstractModel
     *
     * @return Array with all valid locations
     */
    @Override
    public int[] getValidSets() {
        return Arrays.stream(board.getPossibleMoves(playerState.currentPlayer))
                .map(move->new Move(playerState.currentPlayer, move))
                .mapToInt(move->move.location)
                .toArray();
    }

    /**
     * returns the location where the most recent piece is placed.
     * Inherited from AbstractModel
     *
     * @return
     */
    @Override
    public int getSetLocation() {
        return setLocation;
    }

    /**
     * Returns the player who did the most recent move.
     * Inherited from AbstractModel
     *
     * @return
     */
    @Override
    public int getSide() {
        return setSide;
    }

    /**
     * Checks for the end of the match
     *
     * @return
     */
    public boolean checkIfMatchDone() {
        return (board.getPossibleMoves(1).length == 0 && board.getPossibleMoves(2).length == 0);
    }

    /**
     * Returns the given players score
     *
     * @param player
     * @return
     */
    public int getScore(String player) {
        return board.getOccurrences(playerState.playerToInt(player));
    }

    /**
     * Returns the current player
     *
     * @return
     */
    public String getCurrentPlayer() {
        return playerState.playerToString(playerState.currentPlayer);
    }

    /**
     * returns the Client player
     *
     * @return
     */
    public int getClient() {
        return playerState.playerToInt(playerState.CLIENT);
    }

    /**
     * returns the opposing player
     *
     * @return
     */
    public int getOpponent() {
        return playerState.playerToInt(playerState.OPPONENT);
    }

    /**
     * Returns the Board in string form
     *
     * @return
     */
    @Override
    public String toString() {
        return board.toString();
    }

    /**
     * Returns the Board.
     *
     * @return
     */
    public Board getBoard() {
        return this.board;
    }
}