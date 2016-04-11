package othello.ai.minimax;

import othello.Board;
import othello.Game;
import othello.ai.evaluators.PieceCountEvaluator;
import othello.ai.evaluators.PossibleMoveEvaluator;

import java.awt.*;

/**
 * Created by thijs on 10-4-2016.
 */
public class GreedyAI extends MiniMaxAI {

    public GreedyAI(Game game) {
        super(game, 1);
        addEvaluator(new PieceCountEvaluator(), 1);
        addEvaluator(new PossibleMoveEvaluator(), 1);
    }

    @Override
    public int getScore(Board board, int side, Point move) {
        System.out.println(side);
        return super.getScore(board, side, move);
    }
}
