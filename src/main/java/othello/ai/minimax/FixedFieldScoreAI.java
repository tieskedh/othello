package othello.ai.minimax;

import othello.Board;
import othello.Game;
import othello.ai.evaluators.FixedFieldScoreEvaluator;
import othello.ai.evaluators.PieceCountEvaluator;

/**
 * Created by thijs on 11-4-2016.
 */
public class FixedFieldScoreAI extends MiniMaxAI {
    public FixedFieldScoreAI(Game game) {
        super(game, 3);
        addEvaluator(new FixedFieldScoreEvaluator(game), 1);
        addEvaluator(new PieceCountEvaluator(), 1);
    }
}
