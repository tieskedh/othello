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
 * The Class Algorithm.
 */
public abstract class Algorithm implements AI, Evaluator {
    protected Game game;
    protected Board board;
    protected int currentPlayer;
    protected int maxDepth;LinkedHashMap<Evaluator, Integer> evaluators = new LinkedHashMap<>();

    /**
     * Instantiates a new algorithm.
     *
     * @param depth the depth
     * @param game the game
     */
    public Algorithm(int depth, Game game) {
        board = game.getBoard();
        this.maxDepth = depth;
        this.game = game;
    }

    /**
     * Sets the max depth.
     *
     * @param maxDepth the max depth
     * @return the algorithm
     */
    public Algorithm setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    /**
     * Adds the evaluator.
     *
     * @param evaluator the evaluator
     * @param score the score
     * @return the algorithm
     */
    public Algorithm addEvaluator(Evaluator evaluator, int score) {
        evaluators.put(evaluator, score);
        return this;
    }

    /**
     * Removes the evaluator.
     *
     * @param evaluator the evaluator
     * @return the algorithm
     */
    public Algorithm removeEvaluator(Evaluator evaluator) {
        evaluators.remove(evaluator);
        return this;
    }

    /**
     * Sets the score.
     *
     * @param evaluator the evaluator
     * @param score the score
     * @return the algorithm
     */
    public Algorithm setScore(Evaluator evaluator, int score) {
        evaluators.replace(evaluator, score);
        return this;
    }

    /* (non-Javadoc)
     * @see othello.ai.AI#getMove()
     */
    public final String getMove() {
        currentPlayer = game.getClient();
        WeightedMove move = progressStream(getStartingStream(currentPlayer), board, currentPlayer, 1);
        if(move==null) return ""+board.getPossibleMoves(currentPlayer)[0];
        return "" + move.location;
    }

    /**
     * Gets the possible move stream.
     *
     * @param side the side
     * @return the possible move stream
     */
    protected Stream<Point> getPossibleMoveStream(int side) {
        return Arrays.stream(board.getPossibleMoves(side));
    }

    /**
     * Gets the starting stream.
     *
     * @param side the side
     * @return the starting stream
     */
    protected Stream<Point> getStartingStream(int side) {
        return getPossibleMoveStream(side).parallel();
    }

    /**
     * Gets the looping stream.
     *
     * @param side the side
     * @return the looping stream
     */
    protected Stream<Point> getLoopingStream(int side) {
        return getPossibleMoveStream(side);
    }

    /**
     * Progress stream.
     *
     * @param pointStream the point stream
     * @param board the board
     * @param side the side
     * @param depth the depth
     * @return the weighted move
     */
    protected WeightedMove progressStream(Stream<Point> pointStream, Board board, int side, int depth) {
        return pointStream.map(move -> evaluate(board, side, depth, move))
                .filter(Objects::nonNull)
                .max((move1, move2)->move1.getScore()-move2.getScore())
                .orElse(null);
    }

    /**
     * Evaluate.
     *
     * @param board the board
     * @param side the side
     * @param depth the depth
     * @param move the move
     * @return the weighted move
     */
    public abstract WeightedMove evaluate(Board board,int side, int depth, Point move);

    /**
     * Gets the next p layer.
     *
     * @param board the board
     * @param currentPlayer the current player
     * @return the next p layer
     */
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

    /**
     * Gets the score.
     *
     * @param board the board
     * @param side the side
     * @param move the move
     * @param depth the depth
     * @return the score
     */
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

    /* (non-Javadoc)
     * @see othello.ai.botwork.evaluators.Evaluator#getScore(othello.Board, int, java.awt.Point)
     */
    @Override
    public int getScore(Board board, int side, Point move) {
        WeightedMove weightedMove = progressStream(getStartingStream(currentPlayer), board, currentPlayer, 1);
        if(weightedMove==null) return 0;
        return weightedMove.getScore();
    }
}
