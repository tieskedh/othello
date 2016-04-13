package othello.ai.minimax;

import othello.Board;
import othello.Game;
import othello.ai.evaluators.PossibleMoveEvaluator;
import othello.ai.minimax.MiniMaxAI;

import java.awt.*;

/**
 * Created by thijs on 10-4-2016.
 */
public class PossibleMovesAI extends MiniMaxAI {
    public PossibleMovesAI(Game game) {
        super(game, 3);
        addEvaluator(new PossibleMoveEvaluator(), 1);
    }

    @Override
    public int getScore(Board board, int side, Point move, int depth) {
        return super.getScore(board, side, move, depth);
    }
}
