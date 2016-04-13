package othello.ai.minimax;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import othello.Board;
import othello.Game;
import othello.ai.AI;

public class ImprovedGreedyAI implements AI{
	private Game game;
	private boolean timeLeft = true;
	private int runningTime = 0;
	private Random random = new Random();
    private static final int[] RATINGTHIRTY = 			{0, 7, 56, 63};
    private static final int[] RATING_TEN = 			{2, 5, 16, 23, 40, 47, 58, 61};
    private static final int[] RATING_FIVE = 			{3, 4, 18, 21, 24, 31, 32, 39, 42, 45, 59, 60}; 
    private static final int[] RATING_TWO = 			{19, 20, 26, 29, 34, 37, 43, 44};
    private static final int[] RATING_ONE = 			{10, 11, 12, 13, 17, 22, 25, 27, 28, 30, 33, 35, 36, 38, 41, 46, 50, 51, 52, 53};
    private static final int[] RATING_MINUS_TWENTYFIVE = {1, 6, 8, 9, 14, 15, 48, 49, 54, 55, 57, 62};
    private static final int EARLY_GAME_RATIO = 150;
    private static final int MID_GAME_RATIO = 125;
    private static final int POSITION_GAME_RATIO = 140;
	
	public ImprovedGreedyAI(Game game) {
		this.game = game;
	}

	@Override
	public String getMove() {
		timeLeft = true;
		
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
	
	private int getScore(Board board, Point point, int player, int opponent){
		int score = 0;
		int positionScore = 0;
		int gameStateScore = 0;
		int emptyPlaces = board.getEmptySpaces();
		int place = game.pointToInt(point);
		int maxPieces = board.getSize() * board.getSize();
		int pieces = maxPieces - emptyPlaces;
		
		if (emptyPlaces >= 40) {
			gameStateScore = EARLY_GAME_RATIO * (board.getOccurrences(opponent) - board.getOccurrences(player));
		} else {
			gameStateScore = MID_GAME_RATIO * (board.getOccurrences(player) - board.getOccurrences(opponent));
		}
		
		if (placeOnBoardEquals(RATING_ONE, place))
			positionScore = 1;
		else if (placeOnBoardEquals(RATING_FIVE, place))
			positionScore = 5;
		else if (placeOnBoardEquals(RATING_MINUS_TWENTYFIVE, place))
			positionScore = -25;
		else if (placeOnBoardEquals(RATING_TWO, place))
			positionScore = 2;
		else if (placeOnBoardEquals(RATING_TEN, place))
			positionScore = 10;
		else if (placeOnBoardEquals(RATINGTHIRTY, place))
			positionScore = 30;
		
		positionScore = POSITION_GAME_RATIO * positionScore;
		score = positionScore + gameStateScore;
		
		return score;
	}
	
	private boolean placeOnBoardEquals(int[] places, int place) {
		for (int i = 0; i < places.length; i++) {
			if(place == places[i])
				return true;
		}
		return false;
	}
}
