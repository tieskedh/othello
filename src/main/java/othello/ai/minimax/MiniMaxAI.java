package othello.ai.minimax;

import othello.Board;
import othello.Game;
import othello.ai.AI;
import othello.ai.evaluators.Evaluator;
import othello.ai.evaluators.MiniMaxEvaluator;
import othello.utility.WeightedMove;

import java.awt.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by thijs on 11-4-2016.
 */
public class MiniMaxAI implements AI {
    protected Game game;
    protected Board board;
    protected int currentPlayer;
    protected int maxDepth;

    LinkedHashMap<Evaluator, Integer> evaluators = new LinkedHashMap<>();


    public MiniMaxAI(Game game, int depth) {
        this.game = game;
        this.maxDepth = depth;
        board = game.getBoard();
    }

    public MiniMaxAI setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public void addEvaluator(Evaluator evaluator, int score) {
        evaluators.put(evaluator, score);
    }

    public MiniMaxAI removeEvaluator(Evaluator evaluator) {
        evaluators.remove(evaluator);
        return this;
    }

    public void setScore(Evaluator evaluator, int score) {
        evaluators.replace(evaluator, score);
    }

    public final String getMove() {
        currentPlayer = game.getClient();
        WeightedMove move = minimax(game.getBoard(), currentPlayer, 0, new Point(-1,-1));
        if(move.location==-9) return ""+board.getPossibleMoves(currentPlayer)[0];
        return "" + move.location;
    }

    public WeightedMove minimax(Board board, int player, int depth, Point move) {

        WeightedMove maxMove = new WeightedMove(player, move)
                .setScore(Integer.MIN_VALUE);
        Board subBoard = new Board(board);
        if(depth > maxDepth || getNextPLayer(board, player) == 0) {
            return new WeightedMove(player, move)
                    .setScore(getScore(subBoard, player, move, depth));
        } else {
            Point[] possibleMoves = board.getPossibleMoves(player);
            if (!(possibleMoves.length == 0)) {
                for (Point possibleMove : possibleMoves) {
                    subBoard = new Board(board);
                    subBoard.doMove(possibleMove, player);
                    WeightedMove weightedMove = minimax(subBoard, 3 - player, depth + 1, possibleMove)
                         .setLocation(possibleMove);
                    if (maxMove.getScore() > weightedMove.getScore()) {
                        maxMove = weightedMove;
                    }
                }
            } else {
                maxMove.setScore(-1 * getScore(subBoard, player, maxMove.getPoint(), depth+1));
            }
        }
        return maxMove;
    }


    private int getNextPLayer(Board board, int currentPlayer) {
        if(board.getPossibleMoves(3-currentPlayer).length==0) {
            if(board.getPossibleMoves(currentPlayer).length == 0) {
                return 0;
            } else {
                return currentPlayer;
            }
        } else {
            return 3-currentPlayer;
        }
    }

    public int getScore(Board board, int side, Point move, int depth) {
        int score = 0;
        for (Map.Entry<Evaluator, Integer> entry : evaluators.entrySet()) {
            if(entry.getKey() instanceof MiniMaxEvaluator) {
                if(depth< ((MiniMaxEvaluator) entry.getKey()).maxDepth) {
                    score+=entry.getKey().getScore(board, side, move)*entry.getValue();
                }
            } else {
                score+=entry.getKey().getScore(board, side, move)*entry.getValue();
            }
        }
        return score;
    }
}
