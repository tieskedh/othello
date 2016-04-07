import gui.GameView;
import nl.abstractteam.gamemodule.ClientAbstractGameModule;
import nl.abstractteam.gamemodule.MoveListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.LinkedList;

public class GameModule extends ClientAbstractGameModule implements ActionListener {
    private static final int BOARD_SIZE = 8;
    private GameView gameView;

    private HashMap<String, Integer> playerResults = new HashMap<>();
    private String moveDetails;

    private LinkedList<MoveListener> moveListeners = new LinkedList<>();

    public static final String GAME_TYPE = "Reversi";
    private static final String BLACK = "Black";
    private static final String WHITE = "White";
    public static final String[] GAME_PIECES = new String[]{WHITE, BLACK};

    public final Game game;

    /**
     * Mandatory constructor.
     * <p>
     * This function will change the match status to initialized (constant <code>MATCH_INITIALIZED</code>).
     *
     * @param playerOne the first player to play
     * @param playerTwo the second player to play
     */
    public GameModule(String playerOne, String playerTwo) {
        super(playerOne, playerTwo);

        // TODO: 4-4-2016 remove
        System.out.println("GameModule.GameModule");
        System.out.println("playerOne = [" + playerOne + "], playerTwo = [" + playerTwo + "]");


        game = new Game(BOARD_SIZE, playerOne, playerTwo);
    }

    @Override
    public Component getView() {
        return gameView;
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("action performed" + e.getID());
        if (game.isClientsTurn()) {
            for (MoveListener moveListener : moveListeners) {
                moveListener.movePerformed(String.valueOf(e.getID()));
            }
            System.out.println("Move carried out");
        }
    }

    @Override
    public void doPlayerMove(String player, String move) throws IllegalStateException {
        System.out.println("doPlayerMove: " + player + "wants to do move " + move);
        // string in de vorm van 0-63 / 0-8,0-8 binnen
        if (matchStatus != MATCH_STARTED) {
            throw new IllegalStateException("Illegal match state");
        }

        if (!game.getCurrentPlayer().equals(player)) {
            throw new IllegalStateException("IT is not the turn of: " + player);
        }

        System.out.println("Move carried out");
        game.doMove(Integer.parseInt(move));

        if (game.checkIfMatchDone()) {
            matchStatus = MATCH_FINISHED;
            moveDetails = "Done";
            playerResults.put(player, PLAYER_WIN);
            playerResults.put(otherPlayer(player), PLAYER_LOSS);
            System.out.println("MATCH IS OVER");
        } else {
            moveDetails = "Next";
            game.endTurn();
            game.turnStart();
        }
    }

    @Override
    public int getPlayerScore(String player) throws IllegalStateException {
        if (matchStatus != MATCH_FINISHED) {
            throw new IllegalStateException("Illegal match state");
        }
        System.out.println("server called getPlayerScore");
        return game.getScore(player);
    }

    @Override
    public String getMatchResultComment() throws IllegalStateException {
        if (matchStatus != MATCH_FINISHED) {
            throw new IllegalStateException("Illegal match state");
        }
        System.out.println("server called getMatchResultComment");
        return "The match has come to an end.";
    }

    @Override
    public int getMatchStatus() {
        System.out.println("server called getMatchStatus" + matchStatus);
        return matchStatus;
    }

    @Override
    public String getMoveDetails() throws IllegalStateException {
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
        System.out.println(game.getCurrentPlayer() + " should do the next move");
        return game.getCurrentPlayer();
    }

    @Override
    public int getPlayerResult(String player) throws IllegalStateException {
        if (matchStatus != MATCH_FINISHED) {
            throw new IllegalStateException("Illegal match state");
        }
        System.out.println("Server called getPlayerResult");
        return playerResults.get(player);
    }

    @Override
    public String getTurnMessage() throws IllegalStateException {
        if (matchStatus != MATCH_STARTED) {
            throw new IllegalStateException("Illegal match state");
        }
        System.out.println("Server called getTurnMessage");
        return (moveDetails == null) ? "Place your piece." : moveDetails;
    }

    @Override
    public void setClientBegins(boolean clientBegins) {
        game.setClientBegins(clientBegins);
    }

    @Override
    public void setClientPlayPiece(String s) {
        System.out.println(s);
        System.out.println(game.getClient());
        System.out.println(game.getOpponent());
        HashMap<Integer, Icon> players = new HashMap<>();

        ImageIcon black = new ImageIcon(getClass().getResource("black.png"));
        ImageIcon white = new ImageIcon(getClass().getResource("white.png"));

        if (s.equals(BLACK)) {
            players.put(game.getClient(), black);
            players.put(game.getOpponent(), white);
        } else {
            players.put(game.getClient(), white);
            players.put(game.getOpponent(), black);
        }
        System.out.println(players);
        gameView = new GameView(BOARD_SIZE, BOARD_SIZE, players);
        game.addActionListener(gameView);
        gameView.addActionListener(this);
        game.prepareStandardGame();

    }

    @Override // TODO: 4-4-2016 implement 
    public String getAIMove() {
        return null;
    }

    @Override
    public void start() throws IllegalStateException {
        matchStatus = MATCH_STARTED;
        game.turnStart();
    }

    private String otherPlayer(String player) {
        return player.equals(playerOne) ? playerTwo : playerOne;
    }

    public void addMoveListener(MoveListener movelistener) {
        moveListeners.add(movelistener);
    }
}