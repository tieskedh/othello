package othello.ai;

import othello.Board;
import othello.Game;
import othello.utility.WeightedMove;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by thijs on 9-4-2016.
 */
public abstract class AI {
    protected Game game;
    protected Board board;
    protected int currentPlayer;
    protected final int maxDepth;

    public AI(Game game, int depth) {
        this.game = game;
        this.maxDepth = depth;
        board = game.getBoard();
    }

    public final String getMove() {
        currentPlayer = game.getClient();
        WeightedMove move = progressStream(getStartingStream(currentPlayer), board, currentPlayer, 0);
        if(move==null) return ""+board.getPossibleMoves(currentPlayer)[0];
        return "" + move.location;
    }

    private  Stream<Point> getPossibleMoveStream(int side) {
        return Arrays.stream(board.getPossibleMoves(side));
    }

    Stream<Point> getStartingStream(int side) {
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
        Board tempBoard = new Board(board);
        tempBoard.doMove(move.getLocation(), side);

        if(depth < maxDepth) {
            WeightedMove tempMove = progressStream(getLoopingStream(3-side), tempBoard, 3-side, depth+1);
            int score = (tempMove==null)? 0 : tempMove.getScore();
            return new WeightedMove(side, move).setScore(score);
        }
        return new WeightedMove(side, move).setScore(getScore(tempBoard, side));
    }


    public abstract int getScore(Board board, int side);
}
