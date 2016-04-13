package othello.ai;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import othello.Board;
import othello.Game;

public class RandomAI implements AI{
	private Game game;
	private boolean timeLeft = true;
	private int runningTime = 0;
	private Random random = new Random();
	
	public RandomAI(Game game) {
		this.game = game;
	}

	@Override
	public String getMove() {
		timeLeft = true;
		
		Board board = game.getBoard();
		int move = -1;

		timer(3500);
		move = startFindingBestRandomMove(board);
		
		if(move == -1)
			return null;
		return "" + move;
	}
	
	private int startFindingBestRandomMove(Board board) {
		int move = -1;
		int score = Integer.MIN_VALUE;
		HashMap<Integer, Integer> moveScore = new HashMap<>();
		HashMap<Integer, Integer> moveVisit = new HashMap<>();
		
		for (int i = 0; i < 64; i++) {
			moveScore.put(i, 0);
			moveVisit.put(i, 0);
		}
		
		while(timeLeft){
			int[] result = getBestRandomMove(board, true);
			moveScore.put(result[0], moveScore.get(result[0]) + result[1]);
			moveVisit.put(result[0], moveVisit.get(result[0]) + 1);
		}
		
		int tempScore;
		for (int i = 0; i < 64; i++) {
			if (moveVisit.get(i) != 0) {
				tempScore = moveScore.get(i) / moveVisit.get(i);
				if(tempScore > score) {
					score = tempScore;
					move = i;
				}
			}
		}
		
		return move;
	}
	
	private int[] getBestRandomMove(Board board, boolean client) {
		int move = -1;
		int score = Integer.MIN_VALUE;
		
		int[][] boardPieces = Arrays.copyOf(board.getBoardPieces(), board.getBoardPieces().length);
        int[][] oldBoardPieces = new int[8][8];
        
        for (int i = 0; i < boardPieces.length; i++) {
			for (int j = 0; j < boardPieces[i].length; j++) {
				oldBoardPieces[i][j] = boardPieces[i][j];
			}
		}
        
        if (timeLeft) {
        	if (client) {
            	Point[] possibleMoves = board.getPossibleMoves(game.getClient());
            	if (possibleMoves.length > 0) {
            		int randomMove = random.nextInt(possibleMoves.length);
            		move = game.pointToInt(possibleMoves[randomMove]);
            		board.doMoveInternal(possibleMoves[randomMove], game.getClient());
            		score = getBestRandomMove(board, false)[1];
            		board.setBoardPieces(oldBoardPieces);
            	} else {
            		return new int[] {0, board.getOccurrences(game.getClient())};
            	}
        	} else {
        		Point[] possibleMoves = board.getPossibleMoves(game.getOpponent());
            	if (possibleMoves.length > 0) {
            		int randomMove = random.nextInt(possibleMoves.length);
            		move = game.pointToInt(possibleMoves[randomMove]);
            		board.doMoveInternal(possibleMoves[randomMove], game.getClient());
            		score = getBestRandomMove(board, true)[1];
            		board.setBoardPieces(oldBoardPieces);
            	} else {
            		return new int[] {0, board.getOccurrences(game.getClient())};
            	}
        	}
        } else {
        	return new int[] {0, 0};
        }
        
        return new int[] {move, score};
	}
	
	private void timer(int timeInMillis){
		runningTime = 0;
		Runnable thread = () -> {
            while(timeLeft) {
            	try {
            		if(runningTime < timeInMillis)
            			runningTime++;
            		else
            			timeLeft = false;
            		Thread.sleep(1);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
        };
        new Thread(thread).start();
	}
}
