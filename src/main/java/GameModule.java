import gui.GameView;
import nl.abstractteam.gamemodule.ClientAbstractGameModule;
import nl.abstractteam.gamemodule.MoveListener;

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
        game = new Game(BOARD_SIZE, playerOne, playerTwo);

        HashMap<Integer, String> players = new HashMap<>();
        players.put(1, playerOne);
        players.put(2, playerTwo);
        gameView = new GameView(BOARD_SIZE, BOARD_SIZE, players);
        game.addActionListener(gameView);
        gameView.addActionListener(this);
    }


    @Override
    public Component getView() {
        return gameView;
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("doMove  module reached");
        if(game.isClientsTurn()) {
            game.doMove(e.getID());
        }
        for (MoveListener moveListener : moveListeners) {
            moveListener.movePerformed(String.valueOf(e.getID()));
        }
    }

    @Override
    public void doPlayerMove(String player, String move) throws IllegalStateException {
        // string in de vorm van 0-63 / 0-8,0-8 binnen
        if (matchStatus != MATCH_STARTED) {
            throw new IllegalStateException("Illegal match state");
        }

        if (!game.getNextPlayer().equals(player)) {
            throw new IllegalStateException("Not this player's turn.");
        }

        game.doMove(Integer.parseInt(move));
        if (game.checkIfMatchDone()) {
            matchStatus = MATCH_FINISHED;
            moveDetails = "Done";
            playerResults.put(player, PLAYER_WIN);
            playerResults.put(otherPlayer(player), PLAYER_LOSS);
            System.out.println("MATCH IS OVER");
        } else {
            moveDetails = "Next";
        }
    }

    @Override
    public int getPlayerScore(String player) throws IllegalStateException {
        if (matchStatus != MATCH_FINISHED) {
            throw new IllegalStateException("Illegal match state");
        }
        return game.getScore(player);
    }

    @Override
    public String getMatchResultComment() throws IllegalStateException {
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
        return game.getCurrentPlayer();
    }

    @Override
    public int getPlayerResult(String player) throws IllegalStateException {
        if (matchStatus != MATCH_FINISHED) {
            throw new IllegalStateException("Illegal match state");
        }
        return playerResults.get(player);
    }

    @Override
    public String getTurnMessage() throws IllegalStateException {
        if (matchStatus != MATCH_STARTED) {
            throw new IllegalStateException("Illegal match state");
        }
        return (moveDetails == null)? "Place your piece." : moveDetails;
    }

    public void setClientBegins(boolean clientBegins) {
        game.setClientBegins(true);
    }

    @Override // TODO: 4-4-2016 implement 
    public String getAIMove() {
        return null;
    }

    @Override
    public void start() throws IllegalStateException {
        game.prepareStandardGame();
        matchStatus = MATCH_STARTED;
    }

    private String otherPlayer(String player) {
        return player.equals(playerOne) ? playerTwo : playerOne;
    }

    public void addMoveListener(MoveListener movelistener) {
        moveListeners.add(movelistener);
    }
}
