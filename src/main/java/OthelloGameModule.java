import gui.AbstractModel;
import gui.GameView;
import nl.abstractteam.gamemodule.ClientAbstractGameModule;
import nl.abstractteam.gamemodule.MoveListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.math.*;

public class OthelloGameModule extends ClientAbstractGameModule implements ActionListener {
    public static final int BOARDSIZE = 8;
    private GameView gameView;
    public static String playerOne;
    public static String playerTwo;
    private String nextPlayer;
    private HashMap<String, Integer> playerResults;
    private String moveDetails;
    private ActionListener actionListener;
    private LinkedList<ActionListener> actionListeners = new LinkedList<>();
    private LinkedList<MoveListener> moveListeners = new LinkedList<>();

    public static final String GAME_TYPE = "OTHELLO";
    private final OthelloBoard board;

    /**
     * Mandatory constructor.
     * <p>
     * This function will change the match status to initialized (constant <code>MATCH_INITIALIZED</code>).
     *
     * @param playerOne
     * @param playerTwo
     */
    public OthelloGameModule(String playerOne, String playerTwo) {
        super(playerOne, playerTwo);
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        board = new OthelloBoard(BOARDSIZE, playerOne, playerTwo);
        board.setCurrentPlayer(playerOne);
        playerResults = new HashMap<String, Integer>();

        HashMap<Integer, String> players = new HashMap<Integer, String>();
        players.put(1, playerOne);
        players.put(2, playerTwo);
        gameView = new GameView(BOARDSIZE, BOARDSIZE, players);
        board.addActionListener(gameView);
        gameView.addActionListener(this);
    }

    @Override
    public Component getView() {
        return gameView;
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("doMove  module reached");
        doPlayerMove(nextPlayer, String.valueOf(e.getID()));
        for (MoveListener moveListener : moveListeners) {
            moveListener.movePerformed(String.valueOf(e.getID()));
        }
    }

    private void fireEvent(ActionEvent event) {
        actionListener.actionPerformed(event);
    }

    @Override
    public void doPlayerMove(String player, String move) throws IllegalStateException {
        // string in de vorm van 0-63 / 0-8,0-8 binnen
        if (matchStatus != MATCH_STARTED) {
            throw new IllegalStateException("Illegal match state");
        }

        if (nextPlayer != player) {
            throw new IllegalStateException("Not this player's turn.");
        }
        Point movePoint = moveStringToPoint(move);
        board.doMove(movePoint, player);
        if (board.checkIfMatchDone()) {
            matchStatus = MATCH_FINISHED;
            board.turnEnd();
            moveDetails = "Done";
            playerResults.put(player, PLAYER_WIN);
            playerResults.put(otherPlayer(player), PLAYER_LOSS);
        } else {
            moveDetails = "Next";
            board.turnEnd();
            nextPlayer();
            board.turnStart();
        }
    }

    @Override
    public int getPlayerScore(String player) throws IllegalStateException {
        super.getPlayerScore(player);
        if (matchStatus != MATCH_FINISHED) {
            throw new IllegalStateException("Illegal match state");
        }
        return board.getScore(player);

    }

    @Override
    public String getMatchResultComment() throws IllegalStateException {
        super.getMatchResultComment();
        if (matchStatus != MATCH_FINISHED) {
            throw new IllegalStateException("Illegal match state");
        }

        return "The match has come to an end.";
    }

    @Override
    public int getMatchStatus() {
        return matchStatus;
    }

    @Override
    public String getMoveDetails() throws IllegalStateException {
        super.getMoveDetails();
        if (matchStatus == MATCH_INITIALIZED) {
            throw new IllegalStateException("Illegal match state");
        }

        if (moveDetails == null) {
            moveDetails = "Everything is silent. No moves have been performed";
        }
        return moveDetails;
    }

    @Override
    public String getPlayerToMove() throws IllegalStateException {
        if (matchStatus != MATCH_STARTED) {
            throw new IllegalStateException("Illegal match state");
        }

        return nextPlayer;
    }

    @Override
    public int getPlayerResult(String player) throws IllegalStateException {
        super.getPlayerResult(player);
        if (matchStatus != MATCH_FINISHED) {
            throw new IllegalStateException("Illegal match state");
        }

        return playerResults.get(player);
    }

    @Override
    public String getTurnMessage() throws IllegalStateException {
        super.getTurnMessage();
        if (matchStatus != MATCH_STARTED) {
            throw new IllegalStateException("Illegal match state");
        }
        String message = null;

        if (moveDetails == null) {
            message = "Place your piece.";
        } else {
            message = moveDetails;
        }
        return message;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();

        nextPlayer = playerOne;
        board.clearBoard();
        board.prepareStandardGame();

        matchStatus = MATCH_STARTED;
    }

    public Point[] getValidSets(String player) {
        if (matchStatus != MATCH_STARTED) {
            throw new IllegalStateException("Illegal match state");
        }
        return board.getPossibleMoves(player);
    }

    private void nextPlayer() {
        nextPlayer = otherPlayer(nextPlayer);
        board.setCurrentPlayer(nextPlayer);
    }

    private String otherPlayer(String player) {
        return player.equals(playerOne) ? playerTwo : playerOne;
    }

    private Point moveStringToPoint(String move) {
        //expects 0-63
        int moveInt = Integer.parseInt(move);
        int column = (int) Math.floor(moveInt / BOARDSIZE);
        int row = moveInt % BOARDSIZE;
        return new Point(column, row);

    }

    public void addMoveListener(MoveListener movelistener) {
        moveListeners.add(movelistener);
    }

}
