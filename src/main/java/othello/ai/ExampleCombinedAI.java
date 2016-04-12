package othello.ai;

import othello.Board;
import othello.Game;
import othello.ai.evaluators.UnflippablePieceSingularEvaluator;
import othello.ai.evaluators.PossibleMoveEvaluator;
import othello.ai.minimax.MiniMaxAI;

/**
 * Created by thijs on 11-4-2016.
 */
public class ExampleCombinedAI implements AI{

    MiniMaxAI miniMax;
    Game game;
    UnflippablePieceSingularEvaluator unflippablePieceSingularEvaluator;
    public ExampleCombinedAI(Game game) {
        this.game = game;
        miniMax = new MiniMaxAI(game, 1);
        unflippablePieceSingularEvaluator = new UnflippablePieceSingularEvaluator(game.getBoard());
//        miniMax.addEvaluator(new FixedFieldScoreEvaluator(), 1);
        miniMax.addEvaluator(new PossibleMoveEvaluator(), 1);
        miniMax.addEvaluator(new UnflippablePieceSingularEvaluator(game.getBoard()), 2);
    }

    @Override
    public String getMove() {
        return miniMax.setMaxDepth(1).getMove();
    }
}
