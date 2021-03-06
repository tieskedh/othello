package othello.ai.botwork;

import othello.Board;
import othello.Game;
import othello.ai.AI;
import othello.ai.botwork.evaluators.*;
import othello.ai.botwork.algorithms.*;


/**
 * The Class ExampleCombinedAI.
 */
public class ExampleCombinedAI implements AI {

    private final PossibleMoveEvaluator possibleMoveEvaluator;
    Algorithm miniMax;
    Game game;
    Board board;

    int move = 0;
    private final UnflippablePieceAreaEvaluator unflippablePieceAreaEvaluator;
    private final UnflippablePieceSingularEvaluator unflippablePieceSingularEvaluator;
    private final PieceCountEvaluator pieceCountEvaluator;
    private final FixedFieldScoreEvaluator fixedFieldScoreEvaluator;
    private final WallCountEvaluator wallCountEvaluator;
    
    /**
     * Instantiates a new example combined ai.
     *
     * @param game the game
     */
    public ExampleCombinedAI(Game game) {
        this.game = game;
        board = game.getBoard();
        miniMax = new MiniMaxAI(game, 1).setMaxDepth(5);
        unflippablePieceAreaEvaluator = new UnflippablePieceAreaEvaluator(board);
        unflippablePieceSingularEvaluator = new UnflippablePieceSingularEvaluator(board);
        pieceCountEvaluator = new PieceCountEvaluator();
        fixedFieldScoreEvaluator = new FixedFieldScoreEvaluator();
        possibleMoveEvaluator = new PossibleMoveEvaluator();
        wallCountEvaluator = new WallCountEvaluator();
//        miniMax.addEvaluator(new FixedFieldScoreEvaluator(), 1);

    }

    private boolean firstFase;
    private boolean secondFase;
    private boolean thirdFase;
    private boolean fourthFase;

    /* (non-Javadoc)
     * @see othello.ai.AI#getMove()
     */
    @Override
    public String getMove() {
        int emptySpaces = board.getEmptySpaces();
        if(emptySpaces > 50) {
            if(!firstFase) {
                firstFase = true;
                miniMax.addEvaluator(possibleMoveEvaluator, 1);
                miniMax.addEvaluator(pieceCountEvaluator, 3);
                miniMax.addEvaluator(fixedFieldScoreEvaluator, 1);
//                miniMax.addEvaluator(wallCountEvaluator, 100);
            }
        } else if(emptySpaces <= 50 && emptySpaces > 45) {
            if(!secondFase) {
                secondFase = true;
//                miniMax.removeEvaluator(fixedFieldScoreEvaluator);
            }
        } else if(emptySpaces <= 45 && emptySpaces > 8) {
            if(!thirdFase) {
                thirdFase = true;
                miniMax.addEvaluator(unflippablePieceAreaEvaluator, 5);
                miniMax.addEvaluator(unflippablePieceSingularEvaluator, 30);
            }
        } else if(!fourthFase){
            fourthFase = true;
            miniMax.removeEvaluator(unflippablePieceAreaEvaluator)
                    .removeEvaluator(unflippablePieceSingularEvaluator)
                    .removeEvaluator(possibleMoveEvaluator)
                    .setMaxDepth(8);
        }
        return miniMax.getMove();
    }
}
