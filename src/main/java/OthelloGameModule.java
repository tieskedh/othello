import gui.AbstractModel;
import gui.GameView;
import nl.abstractteam.gamemodule.ClientAbstractGameModule;
import nl.abstractteam.gamemodule.MoveListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.math.*;

public class OthelloGameModule
//        extends ClientAbstractGameModule implements ActionListener
 {
//    private static final int BOARDSIZE = 4;
//    private GameView gameView;
//    private static String playerOne;
//    private static String playerTwo;
//    private String nextPlayer;
//    private HashMap<String, Integer> playerResults = new HashMap<>();
//    private String moveDetails;
//    private ActionListener actionListener;
//    private LinkedList<ActionListener> actionListeners = new LinkedList<>();
//    private LinkedList<MoveListener> moveListeners = new LinkedList<>();
//
//    public static final String GAME_TYPE = "OTHELLO";
//    public final OthelloBoard board;
//
//    /**
//     * Mandatory constructor.
//     * <p>
//     * This function will change the match status to initialized (constant <code>MATCH_INITIALIZED</code>).
//     *
//     * @param playerOne the first player to play
//     * @param playerTwo the second player to play
//     */
//    public OthelloGameModule(String playerOne, String playerTwo) {
//        super(playerOne, playerTwo);
//        OthelloGameModule.playerOne = playerOne;
//        OthelloGameModule.playerTwo = playerTwo;
//        board = new OthelloBoard(BOARDSIZE, playerOne, playerTwo);
//        board.setCurrentPlayer(playerOne);
//
//        HashMap<Integer, String> players = new HashMap<>();
//        players.put(1, playerOne);
//        players.put(2, playerTwo);
//        gameView = new GameView(BOARDSIZE, BOARDSIZE, players);
//        board.addActionListener(gameView);
//        gameView.addActionListener(this);
//    }
//
//
//    @Override
//    public Component getView() {
//        return gameView;
//    }
//
//    public void actionPerformed(ActionEvent e) {
//        System.out.println("doMove  module reached");
//        doPlayerMove(nextPlayer, String.valueOf(e.getID()));
//        for (MoveListener moveListener : moveListeners) {
//            moveListener.movePerformed(String.valueOf(e.getID()));
//        }
//    }
//
//    @Override
//    public void doPlayerMove(String player, String move) throws IllegalStateException {
//        // string in de vorm van 0-63 / 0-8,0-8 binnen
//        if (matchStatus != MATCH_STARTED) {
//            throw new IllegalStateException("Illegal match state");
//        }
//
//        if (!nextPlayer.equals(player)) {
//            throw new IllegalStateException("Not this player's turn.");
//        }
//        Point movePoint = moveStringToPoint(move);
//        board.doMove(movePoint, player);
//        if (board.checkIfMatchDone()) {
//            matchStatus = MATCH_FINISHED;
//            board.turnEnd();
//            moveDetails = "Done";
//            playerResults.put(player, PLAYER_WIN);
//            playerResults.put(otherPlayer(player), PLAYER_LOSS);
//            System.out.println("MATCH IS OVER");
//        } else {
//            moveDetails = "Next";
//            board.turnEnd();
//            nextPlayer();
//            board.turnStart();
//        }
//    }
//
//    @Override
//    public int getPlayerScore(String player) throws IllegalStateException {
//        if (matchStatus != MATCH_FINISHED) {
//            throw new IllegalStateException("Illegal match state");
//        }
//        return board.getScore(player);
//
//    }
//
//    @Override
//    public String getMatchResultComment() throws IllegalStateException {
//        if (matchStatus != MATCH_FINISHED) {
//            throw new IllegalStateException("Illegal match state");
//        }
//
//        return "The match has come to an end.";
//    }
//
//    @Override
//    public int getMatchStatus() {
//        return matchStatus;
//    }
//
//    @Override
//    public String getMoveDetails() throws IllegalStateException {
//        if (matchStatus == MATCH_INITIALIZED) {
//            throw new IllegalStateException("Illegal match state");
//        }
//
//        if (moveDetails == null) {
//            moveDetails = "Everything is silent. No moves have been performed";
//        }
//        return moveDetails;
//    }
//
//    @Override
//    public String getPlayerToMove() throws IllegalStateException {
//        if (matchStatus != MATCH_STARTED) {
//            throw new IllegalStateException("Illegal match state");
//        }
//
//        return nextPlayer;
//    }
//
//    @Override
//    public int getPlayerResult(String player) throws IllegalStateException {
//        if (matchStatus != MATCH_FINISHED) {
//            throw new IllegalStateException("Illegal match state");
//        }
//
//        return playerResults.get(player);
//    }
//
//    @Override
//    public String getTurnMessage() throws IllegalStateException {
//        if (matchStatus != MATCH_STARTED) {
//            throw new IllegalStateException("Illegal match state");
//        }
//        return (moveDetails == null)? "Place your piece." : moveDetails;
//    }
//
//    @Override
//    public void start() throws IllegalStateException {
//        nextPlayer = playerOne;
//        board.clearBoard();
////        board.prepareStandardGame();
//        board.prepareTestGame();
//        board.turnStart();
//        matchStatus = MATCH_STARTED;
//    }
//
//    /**
//     * TODO: 1-4-2016 make private
//     */
//    void nextPlayer() {
//        nextPlayer = otherPlayer(nextPlayer);
//        board.setCurrentPlayer(nextPlayer);
//    }
//
//    private String otherPlayer(String player) {
//        return player.equals(playerOne) ? playerTwo : playerOne;
//    }
//
//    private Point moveStringToPoint(String move) {
//        //expects 0-63
//        int moveInt = Integer.parseInt(move);
//        return new Point(
//            moveInt / BOARDSIZE,
//            moveInt % BOARDSIZE
//        );
//    }
//
//    public void addMoveListener(MoveListener movelistener) {
//        moveListeners.add(movelistener);
//    }

}
