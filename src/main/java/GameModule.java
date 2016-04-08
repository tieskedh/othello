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
		System.out.println("Othello AI -> I'm asked to do a move");
		Board board = game.getBoard();

		System.out.println(board.getBoardPieces());
		int[][] boardPieces = Arrays.copyOf(board.getBoardPieces(), board.getBoardPieces().length);

		int[][] newBoardPieces = new int[8][8];
		for (int i = 0; i < boardPieces.length; i++) {
			for (int j = 0; j < boardPieces[i].length; j++) {
				newBoardPieces[i][j] = boardPieces[i][j];
			}
		}

		int[] possibleMoves = game.getValidSets();

		int score = 0;
		Best move;

		/*
		 * Greedy for(int i = 0; i < possibleMoves.length; i++) {
		 * board.doMove(game.intToPoint(possibleMoves[i]), game.getClient());
		 * if(board.getOccurrences(game.getClient()) > score) { score =
		 * board.getOccurrences(game.getClient()); move = possibleMoves[i]; }
		 * board.setBoardPieces(newBoardPieces); }
		 */

		move = getBestMiniMaxMove(board, 0);

		if (move.move == -1) {
			return null;
		}
		return "" + move.move;
	}

	private Best getBestMiniMaxMove(Board board, int depth) {
		Best reply = null;
		if (depth < 5) {
			Best temporaryBest;
			int score = -1;
			int[][] boardPieces = board.getBoardPieces();

			int[][] newBoardPieces = new int[8][8];
			for (int i = 0; i < boardPieces.length; i++) {
				for (int j = 0; j < boardPieces[i].length; j++) {
					newBoardPieces[i][j] = boardPieces[i][j];
				}
			}

			int[] possibleMoves = game.getValidSets();
			for (int i = 0; i < possibleMoves.length; i++) {
				temporaryBest = getBestMiniMaxMove(board, (depth +1));
				temporaryBest.move = possibleMoves[i];
				
				if (reply == null) {
					reply = temporaryBest;
				} else if (game.isClientsTurn()) {
					board.doMove(game.intToPoint(possibleMoves[i]), game.getClient());
					temporaryBest.value = board.getOccurrences(game.getClient());
					if (reply.value < temporaryBest.value){
						reply = temporaryBest;
					}
				} else {
					board.doMove(game.intToPoint(possibleMoves[i]), game.getOpponent());
					temporaryBest.value = board.getOccurrences(game.getClient());
					if (reply.value < temporaryBest.value){
						reply = temporaryBest;
					}
				}

				board.setBoardPieces(newBoardPieces);
			}
		} else {
			return new Best(0);
		}

		return reply;
	}

	// called 3rd
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