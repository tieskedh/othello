package othello.ai;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;

import othello.Board;
import othello.Game;

public class ImprovedGreedyAI implements AI{
	private static final int[] RATING_THIRTY = { 0, 7, 56, 63 };
	private static final int[] RATING_TEN = { 2, 5, 16, 23, 40, 47, 58, 61 };
	private static final int[] RATING_FIVE = { 3, 4, 18, 21, 24, 31, 32, 39, 42, 45, 59, 60 };
	private static final int[] RATING_TWO = { 19, 20, 26, 29, 34, 37, 43, 44 };
	private static final int[] RATING_ONE = { 10, 11, 12, 13, 17, 22, 25, 27, 28, 30, 33, 35, 36, 38, 41, 46, 50, 51,
			52, 53 };
	private static final int[] RATING_MINUS_TWENTYFIVE = { 1, 6, 8, 9, 14, 15, 48, 49, 54, 55, 57, 62 };
    private static final int EARLY_GAME_RATIO = 150;
    private static final int MID_GAME_RATIO = 125;
    private static final int POSITION_GAME_RATIO = 140;
	private Game game;
	private HashMap<Integer, Integer> positionRating = new HashMap<>();
	
	public ImprovedGreedyAI(Game game) {
		this.game = game;
		buildPositionsRating();
	}

	@Override
	public String getMove() {		
		Board board = game.getBoard();
		int move = -1;
		
		move = getBestGreedyMove(board);
		
		if(move == -1)
			return null;
		return "" + move;
	}
	
	private int getBestGreedyMove(Board board){
        int[][] boardPieces = Arrays.copyOf(board.getBoardPieces(), board.getBoardPieces().length);

        int[][] newBoardPieces = new int[8][8];
        for (int i = 0; i < boardPieces.length; i++) {
            System.arraycopy(boardPieces[i], 0, newBoardPieces[i], 0, boardPieces[i].length);
        }

        Point[] possibleMoves = board.getPossibleMoves(game.getClient());

        int score = Integer.MIN_VALUE;
        int tempScore;
        int move = -1;
        int opponentPossibleMoves = Integer.MAX_VALUE;
        int tempOpponentPossibleMoves;
        
        for (Point possibleMove : possibleMoves) {
            board.doMoveInternal(possibleMove, game.getClient());
            tempScore = board.getOccurrences(game.getClient());
            tempScore += getScore(board, possibleMove, game.getClient(), game.getOpponent());
            if (board.getEmptySpaces() > 55) {
            	tempOpponentPossibleMoves = board.getPossibleMoves(game.getOpponent()).length;
            	if(board.getPossibleMoves(game.getOpponent()).length < opponentPossibleMoves) {
            		opponentPossibleMoves = tempOpponentPossibleMoves;
            		move = game.pointToInt(possibleMove);
            	}
            } else {
            	if (tempScore > score) {
	            	score = tempScore;
	            	move = game.pointToInt(possibleMove);
	            }
            }
            board.setBoardPieces(newBoardPieces);
        }
        return move;
    }
	
	private int getScore(Board board, Point point, int player, int opponent) {
		int[][] boardPieces = board.getBoardPieces();
		int[][] oldBoardPieces = new int[8][8];
		for (int i = 0; i < boardPieces.length; i++) {
			System.arraycopy(boardPieces[i], 0, oldBoardPieces[i], 0, boardPieces[i].length);
		}

		Point[] possibleMoves = board.getPossibleMoves(opponent);
		for (Point possibleMove : possibleMoves) {
			board.doMoveInternal(possibleMove, opponent);
			if (board.getOccurrences(player) == 0) {
				return -100;
			}
		}

		int score = 0;
		int positionScore = 0;
		int gameStateScore = 0;
		int emptyPlaces = board.getEmptySpaces();
		int place = game.pointToInt(point);

		if (emptyPlaces >= 40) {
			gameStateScore = EARLY_GAME_RATIO * (board.getOccurrences(opponent) - board.getOccurrences(player));
		} else {
			gameStateScore = MID_GAME_RATIO * (board.getOccurrences(player) - board.getOccurrences(opponent));
		}

		positionScore = POSITION_GAME_RATIO * positionRating.get(place);
		score = positionScore + gameStateScore;

		return score;
	}

	private void buildPositionsRating(){
		for (int i = 0; i < ImprovedGreedyAI.RATING_THIRTY.length; i++) {
			positionRating.put(ImprovedGreedyAI.RATING_THIRTY[i], 30);
		}
		for (int i = 0; i < ImprovedGreedyAI.RATING_TEN.length; i++) {
			positionRating.put(ImprovedGreedyAI.RATING_TEN[i], 10);
		}
		for (int i = 0; i < ImprovedGreedyAI.RATING_FIVE.length; i++) {
			positionRating.put(ImprovedGreedyAI.RATING_FIVE[i], 5);
		}
		for (int i = 0; i < ImprovedGreedyAI.RATING_TWO.length; i++) {
			positionRating.put(ImprovedGreedyAI.RATING_TWO[i], 2);
		}
		for (int i = 0; i < ImprovedGreedyAI.RATING_ONE.length; i++) {
			positionRating.put(ImprovedGreedyAI.RATING_ONE[i], 1);
		}
		for (int i = 0; i < ImprovedGreedyAI.RATING_MINUS_TWENTYFIVE.length; i++) {
			positionRating.put(ImprovedGreedyAI.RATING_MINUS_TWENTYFIVE[i], -25);
		}
	}
}
