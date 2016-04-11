package othello.ai.minimax;

import othello.Board;
import othello.Game;
import othello.ai.evaluators.PieceCountEvaluator;
import othello.ai.evaluators.PossibleMoveEvaluator;

/**
 * Created by thijs on 10-4-2016.
 */
public class GreedyAI extends MiniMaxAI {

    public GreedyAI(Game game) {
        super(game, 5);
        addEvaluator(new PieceCountEvaluator(), 1);
        addEvaluator(new PossibleMoveEvaluator(), 1);
    }
}
