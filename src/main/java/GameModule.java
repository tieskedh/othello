import gui.AbstractModel;
import gui.GameView;
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
	public static final String[] GAME_PIECES = new String[] { WHITE, BLACK };

	public final Game game;

	/**
	 * Mandatory constructor.
	 * <p>
	 * This function will change the match status to initialized (constant
	 * <code>MATCH_INITIALIZED</code>).
	 *
	 * @param playerOne
	 *            the first player to play
	 * @param playerTwo
	 *            the second player to play
	 */
	public GameModule(String playerOne, String playerTwo) {
		super(playerOne, playerTwo);

		game = new Game(BOARD_SIZE, playerOne, playerTwo);
	}

	@Override
	public Component getView() {
		return gameView;
	}

	public void actionPerformed(ActionEvent e) {
		if (game.isClientsTurn()) {
			for (MoveListener moveListener : moveListeners) {
				moveListener.movePerformed(String.valueOf(e.getID()));
			}
		}
	}

	@Override
	public void doPlayerMove(String player, String move) throws IllegalStateException {
		game.clearMoves();

		game.fireEvents();

		// string in de vorm van 0-63 / 0-8,0-8 binnen
		if (matchStatus != MATCH_STARTED) {
			throw new IllegalStateException("Illegal match state");
		}

		if (!game.getCurrentPlayer().equals(player)) {
			throw new IllegalStateException("IT is not the turn of: " + player);
		}

		game.doMove(Integer.parseInt(move));
		game.fireEvents();

		if (game.checkIfMatchDone()) {
			matchStatus = MATCH_FINISHED;
			moveDetails = "Done";
			playerResults.put(player, PLAYER_WIN);
			playerResults.put(otherPlayer(player), PLAYER_LOSS);
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
		return (moveDetails == null) ? "Place your piece." : moveDetails;
	}

	// called 1st
	@Override
	public void setClientBegins(boolean clientBegins) {
		game.setClientBegins(clientBegins);
	}

	// called 2nd
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
		gameView = new GameView(BOARD_SIZE, BOARD_SIZE, players);
		game.addActionListener(gameView);
		gameView.addActionListener(this);
	}

	@Override // TODO: 4-4-2016 implement
	public String getAIMove() {
		Board board = game.getBoard();
		int move;

		move = getBestMiniMaxMove(board, 0, game.getClient(), game.getOpponent(), game.getClient())[1];
		game.getClient();
		if (move == -1) {
			return null;
		}
		
		System.out.println("AI decides the move needs to be: " + move);
		return "" + move;
	}

	private int[] getBestMiniMaxMove(Board board, int depth, int client, int opponent, int turn) {
		int currentScore;
		int bestScore = (turn == client) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		int bestMove = -1;
		
		int[] possibleMoves = game.getValidSets();
		
		if (depth < 5 && possibleMoves.length > 0) {			
			int[][] boardPieces = board.getBoardPieces();
			int[][] oldBoardPieces = new int[8][8];
			for (int i = 0; i < boardPieces.length; i++) {
				for (int j = 0; j < boardPieces[i].length; j++) {
					oldBoardPieces[i][j] = boardPieces[i][j];
				}
			}

			for (int currentMove : possibleMoves) {
				board.doMoveInternal(game.intToPoint(currentMove), turn);
				if (turn == client) {
					currentScore = getBestMiniMaxMove(board, (depth + 1), client, opponent, switchSide(turn, client, opponent))[0];
					if (currentScore > bestScore){
						bestMove = currentMove;
						bestScore = currentScore;
					}
				} else {
					currentScore = getBestMiniMaxMove(board, (depth + 1), client, opponent, switchSide(turn, client, opponent))[0];
					if (currentScore > bestScore){
						bestMove = currentMove;
						bestScore = currentScore;
					}
				}

				board.setBoardPieces(oldBoardPieces);
			}
		} else {
			if(depth % 2 > 0)
				return new int[] {board.getOccurrences(opponent), bestMove};
			return new int[] {board.getOccurrences(client), bestMove};
		}

		return new int[] {bestScore, bestMove};
	}
	
	private int switchSide(int turn, int client, int opponent){
		if(turn != client){
			return client;
		}
		return opponent;
	}

	// called 3rd
	@Override
	public void start() throws IllegalStateException {
		game.prepareStandardGame();
		game.turnStart();
		matchStatus = MATCH_STARTED;

	}

	private String otherPlayer(String player) {
		return player.equals(playerOne) ? playerTwo : playerOne;
	}

	public void addMoveListener(MoveListener movelistener) {
		moveListeners.add(movelistener);
	}
	
	private class Best {
        int move;
        int value;

        public Best(int value) {
            this(0, value);
        }

        public Best(int move, int value) {
            this.value = value;
            this.move = move;
        }
    }
}