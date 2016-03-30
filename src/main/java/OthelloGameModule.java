import java.awt.*;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.math.*;

public class OthelloGameModule extends AbstractGameModule implements Observer {
    public static final int BOARDSIZE = 8;
    protected OthelloBoard board;
    public static String playerOne;
    public static String playerTwo;
    private String nextPlayer;
    private HashMap<String, Integer> playerResults;
    private String moveDetails;

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
        OthelloBoard board = new OthelloBoard(BOARDSIZE);
        playerResults = new HashMap<String, Integer>();

    }

    @Override
    public Component getView() {

        /// give view
return null;
    }

    @Override
    public void doPlayerMove(String player, String move) throws IllegalStateException {
        super.doPlayerMove(player, move);
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
            moveDetails = "Klaar";
            playerResults.put(player, PLAYER_WIN);
            playerResults.put(otherPlayer(player), PLAYER_LOSS);
        } else {
            moveDetails = "Volgende";
            nextPlayer();
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
        if (matchStatus != MATCH_INITIALIZED) {
            throw new IllegalStateException("Illegal match state");
        }
        nextPlayer = playerOne;
        board.clearBoard();
        board.prepareStandardGame();

        matchStatus = MATCH_STARTED;
    }

    @Override
    public void update(Observable o, Object arg) {

    }



    public Point[] getValidMoves(String player) {
        if (matchStatus != MATCH_STARTED) {
            throw new IllegalStateException("Illegal match state");
        }
        return board.getPossibleMoves(player);
    }

    private void nextPlayer() {
        nextPlayer = otherPlayer(nextPlayer);
    }

    private String otherPlayer(String player) {
        return player.equals(playerOne) ? playerTwo : playerOne;
    }

    private Point moveStringToPoint(String move) {
        //expects 0-63
        int moveInt = Integer.parseInt(move);
        moveInt++;
        //moveInt == 1-64
        int column = (int) Math.floor(moveInt / BOARDSIZE);
        int row = moveInt % BOARDSIZE;
        return new Point(column, row);

    }
}
