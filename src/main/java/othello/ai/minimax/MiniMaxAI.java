package othello.ai.minimax;

import othello.Board;
import othello.Game;
import othello.ai.AI;
import othello.ai.evaluators.Evaluator;
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

    public void removeEvaluator(Evaluator evaluator) {
        evaluators.remove(evaluator);
    }

    public void setScore(Evaluator evaluator, int score) {
        evaluators.replace(evaluator, score);
    }

    public final String getMove() {
        currentPlayer = game.getClient();
        WeightedMove move = progressStream(getStartingStream(currentPlayer), board, currentPlayer, 1);
        if(move==null) return ""+board.getPossibleMoves(currentPlayer)[0];
        return "" + move.location;
    }


    private Stream<Point> getPossibleMoveStream(int side) {
        return Arrays.stream(board.getPossibleMoves(side));
    }

    private Stream<Point> getStartingStream(int side) {
        return getPossibleMoveStream(side).parallel();
    }

    private Stream<Point> getLoopingStream(int side) {
        return getPossibleMoveStream(side);
    }

    private WeightedMove progressStream(Stream<Point> pointStream, Board board, int side, int depth) {
        return pointStream.map(move -> evaluate(board, side, depth, move))
                .filter(Objects::nonNull)
                .min((move1, move2)->move1.getScore()-move2.getScore())
                .orElse(null);
    }

    private WeightedMove evaluate(Board board, int side, int depth, Point move) {
        //new board
        Board tempBoard = new Board(board);
        tempBoard.doMove(move.getLocation(), side);

        //if depth < maxDepth
        if(depth < maxDepth) {

            //for each possible move, get the maximum evaluation
            int opponent = getNextPLayer(board, side);
            if(opponent==0) {
                return new WeightedMove(side, move)
                        .setScore(getScore(board, side, move));
            } else {
                WeightedMove tempMove = progressStream(getLoopingStream(opponent), tempBoard, opponent, depth+1);
                int score = (tempMove==null)? 0 : tempMove.getScore();
                score += getScore(board, side,move);
                return new WeightedMove(side, move).setScore(score);
            }
        }
        return new WeightedMove(side, move).setScore(getScore(tempBoard, side, move));
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

    public int getScore(Board board, int side, Point move) {
        int score = 0;
        for (Map.Entry<Evaluator, Integer> entry : evaluators.entrySet()) {
            score+=entry.getKey().getScore(board, side, move)*entry.getValue();
        }
        return score;
    }
}
