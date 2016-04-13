package othello.ai.minimax;

import othello.Game;
import othello.ai.evaluators.UnflippablePieceSingularEvaluator;

/**
 * Created by thijs on 11-4-2016.
 */
public class BoardStabilityAI extends MiniMaxAI{
    public BoardStabilityAI(Game game) {
        super(game, 1);
        addEvaluator(new UnflippablePieceSingularEvaluator(game.getBoard()), 1);
    }
}
