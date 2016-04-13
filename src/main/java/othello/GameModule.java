package othello;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import nl.abstractteam.gamemodule.ClientAbstractGameModule;
import nl.abstractteam.gamemodule.MoveListener;
import othello.ai.AI;

import othello.ai.StandardCombinedAI;
import othello.gui.GameView;

/**
 * The Class GameModule.
 */
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
    public AI ai;

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

        Board board = new Board(BOARD_SIZE);
        game = new Game(playerOne, playerTwo);
        game.setBoard(board);
        board.addActionListener(game);

        gameView = new GameView(BOARD_SIZE, BOARD_SIZE);
        game.addActionListener(gameView);
        gameView.addActionListener(this);
    }

    /* (non-Javadoc)
     * @see nl.hanze.t23i.gamemodule.extern.IGameModule#getView()
     */
    @Override
    public Component getView() {
        return gameView;
    }

    /**
     * Sends action to all MoveListeners.
     * currently contains only the Framework.
     *
     * @param e the e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (game.isClientsTurn()) {
            for (MoveListener moveListener : moveListeners) {
                moveListener.movePerformed(String.valueOf(e.getID()));
            }
        }
    }

    /* (non-Javadoc)
     * @see nl.hanze.t23i.gamemodule.extern.AbstractGameModule#doPlayerMove(java.lang.String, java.lang.String)
     */
    @Override
    public void doPlayerMove(String player, String move) throws IllegalStateException {
        game.fireEvents(true);
        int intMove;
        try {
            intMove = Integer.parseInt(move);
        } catch (Exception e) {
            throw new IllegalStateException("Move not Integer format.");
        }

        // string in de vorm van 0-63 / 0-8,0-8 binnen
        if (matchStatus != MATCH_STARTED) {
            throw new IllegalStateException("Illegal match state");
        }

        if (!game.getCurrentPlayer().equals(player)) {
            throw new IllegalStateException("IT is not the turn of: " + player);
        }

        if (!(intMove >= 0 && intMove <= 63)) {
            throw new IllegalStateException("Move outside boundaries of 0-63");
        }

        System.out.println("Move carried out");
        game.doMove(Integer.parseInt(move));
        game.fireEvents(false);

        game.endTurn();
        //Checks and handles the end of the match.
        if (game.checkIfMatchDone()) {
            matchStatus = MATCH_FINISHED;
            moveDetails = "Done";
            playerResults.put(player, PLAYER_WIN);
            playerResults.put(otherPlayer(player), PLAYER_LOSS);
        } else {
            moveDetails = "Next";
            game.turnStart();
        }
    }

    /* (non-Javadoc)
     * @see nl.hanze.t23i.gamemodule.extern.AbstractGameModule#getPlayerScore(java.lang.String)
     */
    @Override
    public int getPlayerScore(String player) throws IllegalStateException {
        if (matchStatus != MATCH_FINISHED) {
            throw new IllegalStateException("Illegal match state");
        }
        return game.getScore(player);
    }

    /* (non-Javadoc)
     * @see nl.hanze.t23i.gamemodule.extern.AbstractGameModule#getMatchResultComment()
     */
    @Override
    public String getMatchResultComment() throws IllegalStateException {
        if (matchStatus != MATCH_FINISHED) {
            throw new IllegalStateException("Illegal match state");
        }
        return "The match has come to an end.";
    }

    /* (non-Javadoc)
     * @see nl.hanze.t23i.gamemodule.extern.AbstractGameModule#getMatchStatus()
     */
    @Override
    public int getMatchStatus() {
        return matchStatus;
    }

    /* (non-Javadoc)
     * @see nl.hanze.t23i.gamemodule.extern.AbstractGameModule#getMoveDetails()
     */
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

    /* (non-Javadoc)
     * @see nl.hanze.t23i.gamemodule.extern.AbstractGameModule#getPlayerToMove()
     */
    @Override
    public String getPlayerToMove() throws IllegalStateException {
        if (matchStatus != MATCH_STARTED) {
            throw new IllegalStateException("Illegal match state");
        }
        return game.getCurrentPlayer();
    }

    /* (non-Javadoc)
     * @see nl.hanze.t23i.gamemodule.extern.AbstractGameModule#getPlayerResult(java.lang.String)
     */
    @Override
    public int getPlayerResult(String player) throws IllegalStateException {
        if (matchStatus != MATCH_FINISHED) {
            throw new IllegalStateException("Illegal match state");
        }
        return playerResults.get(player);
    }

    /* (non-Javadoc)
     * @see nl.hanze.t23i.gamemodule.extern.AbstractGameModule#getTurnMessage()
     */
    @Override
    public String getTurnMessage() throws IllegalStateException {
        if (matchStatus != MATCH_STARTED) {
            throw new IllegalStateException("Illegal match state");
        }
        return (moveDetails == null) ? "Place your piece." : moveDetails;
    }

    /* (non-Javadoc)
     * @see nl.abstractteam.gamemodule.ClientAbstractGameModule#setClientBegins(boolean)
     */
    // called 1st
    @Override
    public void setClientBegins(boolean clientBegins) {
        game.setClientBegins(clientBegins);
        ai = new StandardCombinedAI(game);
    }

    /* (non-Javadoc)
     * @see nl.abstractteam.gamemodule.ClientAbstractGameModule#setClientPlayPiece(java.lang.String)
     */
    //called 2nd
    @Override
    public void setClientPlayPiece(String s) {
        HashMap<Integer, Icon> players = new HashMap<>();

        ImageIcon black = new ImageIcon(getClass().getResource("/black.png"));
        ImageIcon white = new ImageIcon(getClass().getResource("/white.png"));

        if (s.equals(BLACK)) {
            players.put(game.getClient(), black);
            players.put(game.getOpponent(), white);
        } else {
            players.put(game.getClient(), white);
            players.put(game.getOpponent(), black);
        }

        gameView.setPlayers(players);
    }

    /**
     * Contains the logic for the AI players.
     * If the Client is set as an AI, the Framework will use this method to acquire its move, instead of using a View Event
     * Inherited from ClientAbstractGameModule.
     *
     * @return the AI move
     */
    @Override // TODO: 4-4-2016 implement
    public String getAIMove() {
        return ai.getMove();
    }

    /* (non-Javadoc)
     * @see nl.hanze.t23i.gamemodule.extern.AbstractGameModule#start()
     */
    //called 3rd
    @Override
    public void start() throws IllegalStateException {
        game.prepareStandardGame();
        game.turnStart();
        matchStatus = MATCH_STARTED;

    }

    /**
     * Other player.
     *
     * @param player the player
     * @return the string
     */
    private String otherPlayer(String player) {
        return player.equals(playerOne) ? playerTwo : playerOne;
    }

    /* (non-Javadoc)
     * @see nl.abstractteam.gamemodule.ClientAbstractGameModule#addMoveListener(nl.abstractteam.gamemodule.MoveListener)
     */
    public void addMoveListener(MoveListener movelistener) {
        moveListeners.add(movelistener);
    }


}