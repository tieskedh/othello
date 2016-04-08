package othello;

import othello.gui.AbstractModel;
import othello.gui.GameView;
import nl.abstractteam.gamemodule.ClientAbstractGameModule;
import nl.abstractteam.gamemodule.MoveListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
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

    /**
     * Sends action to all MoveListeners.
     * currently contains only the Framework.
     *
     * @param e
     */
    @Override
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
        game.clearMoves();
        System.out.println("doPlayerMove: " + player + "wants to do move " + move);

        game.fireEvents();

        // string in de vorm van 0-63 / 0-8,0-8 binnen
        if (matchStatus != MATCH_STARTED) {
            throw new IllegalStateException("Illegal match state");
        }

        if (!game.getCurrentPlayer().equals(player)) {
            throw new IllegalStateException("IT is not the turn of: " + player);
        }

        System.out.println("Move carried out");
        game.doMove(Integer.parseInt(move));
        game.fireEvents();

        //Checks and handles the end of the match.
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

    // called 1st
    @Override
    public void setClientBegins(boolean clientBegins) {
        System.out.println("setClientBeginsBEGINS!");
        game.setClientBegins(clientBegins);
    }

    //called 2nd
    @Override
    public void setClientPlayPiece(String s) {
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

        gameView.setPlayers(players);
    }


    /**
     * Contains the logic for the AI players.
     * If the Client is set as an AI, the Framework will use this method to acquire its move, instead of using a View Event
     * Inherited from ClientAbstractGameModule.
     *
     * @return
     */
    @Override // TODO: 4-4-2016 implement
    public String getAIMove() {
        System.out.println("Othello AI -> I'm asked to do a move");
        Board board = game.getBoard();

        int[][] boardPieces = Arrays.copyOf(board.getBoardPieces(), board.getBoardPieces().length);

        int[][] newBoardPieces = new int[8][8];
        for (int i = 0; i < boardPieces.length; i++) {
            System.arraycopy(boardPieces[i], 0, newBoardPieces[i], 0, boardPieces[i].length);
        }

        int[] possibleMoves = game.getValidSets();

        int score = Integer.MIN_VALUE;
        int tempScore;
        int move = -1;
        for (int possibleMove : possibleMoves) {
            board.doMove(game.intToPoint(possibleMove), game.getClient());
            tempScore = board.getOccurrences(game.getClient());
            if (tempScore > score) {
                score = tempScore;
                move = possibleMove;
            }
            board.setBoardPieces(newBoardPieces);
        }

        if (move == -1) {
            return null;
        }
        return "" + move;
    }

    //called 3rd
    @Override
    public void start() throws IllegalStateException {
        game.prepareStandardGame();
        System.out.println(game);
        game.turnStart();
        matchStatus = MATCH_STARTED;

    }

    private String otherPlayer(String player) {
        return player.equals(playerOne) ? playerTwo : playerOne;
    }

    public void addMoveListener(MoveListener movelistener) {
        moveListeners.add(movelistener);
    }
}