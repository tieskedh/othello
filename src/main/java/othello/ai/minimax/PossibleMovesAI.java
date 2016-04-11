package othello.ai.minimax;

import othello.Game;
import othello.ai.evaluators.PossibleMoveEvaluator;
import othello.ai.minimax.MiniMaxAI;

/**
 * Created by thijs on 10-4-2016.
 */
public class PossibleMovesAI extends MiniMaxAI {



    public PossibleMovesAI(Game game) {
        super(game, 3);
        addEvaluator(new PossibleMoveEvaluator(), 1);
    }
}
