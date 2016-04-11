package othello.ai;

import othello.Board;
import othello.Game;
import othello.ai.evaluators.FixedFieldScoreEvaluator;
import othello.ai.evaluators.PossibleMoveEvaluator;
import othello.ai.minimax.FixedFieldScoreAI;
import othello.ai.minimax.GreedyAI;
import othello.ai.minimax.MiniMaxAI;

/**
 * Created by thijs on 11-4-2016.
 */
public class ExampleCombinedAI implements AI{

    MiniMaxAI miniMax;
    Game game;

    public ExampleCombinedAI(Game game) {
        this.game = game;
        miniMax = new MiniMaxAI(game, 1);
        miniMax.addEvaluator(new FixedFieldScoreEvaluator(game), 1);
        miniMax.addEvaluator(new PossibleMoveEvaluator(), 1);
    }

    @Override
    public String getMove() {
        Board board = game.getBoard();
        if(board.getEmptySpaces() <= 11) {
            return miniMax.setMaxDepth(10)
                    .getMove();
        } else {
            return miniMax.setMaxDepth(11)
                    .getMove();
        }
    }
}
