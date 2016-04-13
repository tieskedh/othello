package othello.ai.botwork.algorithms;

import othello.Board;
import othello.Game;
import othello.ai.AI;
import othello.ai.botwork.evaluators.Evaluator;
import othello.ai.botwork.evaluators.MaxDepthEvaluator;
import othello.utility.WeightedMove;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by thijs on 13-4-2016.
 */
public abstract class Algorithm implements AI, Evaluator {
    protected Game game;
    protected Board board;
    protected int currentPlayer;
    protected int maxDepth;LinkedHashMap<Evaluator, Integer> evaluators = new LinkedHashMap<>();

    public Algorithm(int depth, Game game) {
        board = game.getBoard();
        this.maxDepth = depth;
        this.game = game;
    }

    public Algorithm setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public Algorithm addEvaluator(Evaluator evaluator, int score) {
        evaluators.put(evaluator, score);
        return this;
    }

    public Algorithm removeEvaluator(Evaluator evaluator) {
        evaluators.remove(evaluator);
        return this;
    }

    public Algorithm setScore(Evaluator evaluator, int score) {
        evaluators.replace(evaluator, score);
        return this;
    }

    public final String getMove() {
        currentPlayer = game.getClient();
        WeightedMove move = progressStream(getStartingStream(currentPlayer), board, currentPlayer, 1);
        if(move==null) return ""+board.getPossibleMoves(currentPlayer)[0];
        return "" + move.location;
    }

    protected Stream<Point> getPossibleMoveStream(int side) {
        return Arrays.stream(board.getPossibleMoves(side));
    }

    protected Stream<Point> getStartingStream(int side) {
        return getPossibleMoveStream(side).parallel();
    }

    protected Stream<Point> getLoopingStream(int side) {
        return getPossibleMoveStream(side);
    }

    protected WeightedMove progressStream(Stream<Point> pointStream, Board board, int side, int depth) {
        return pointStream.map(move -> evaluate(board, side, depth, move))
                .filter(Objects::nonNull)
                .max((move1, move2)->move1.getScore()-move2.getScore())
                .orElse(null);
    }

    public abstract WeightedMove evaluate(Board board,int side, int depth, Point move);

    protected int getNextPLayer(Board board, int currentPlayer) {
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
            if(entry.getKey() instanceof MaxDepthEvaluator) {
                if(depth <= ((MaxDepthEvaluator) entry.getKey()).getMaxDepth()) {
                    score+=entry.getKey().getScore(board, side, move)*entry.getValue();
                }
            } else {
                score+=entry.getKey().getScore(board, side, move)*entry.getValue();
            }
        }
        return score;
    }

    @Override
    public int getScore(Board board, int side, Point move) {
        WeightedMove weightedMove = progressStream(getStartingStream(currentPlayer), board, currentPlayer, 1);
        if(weightedMove==null) return 0;
        return weightedMove.getScore();
    }
}
