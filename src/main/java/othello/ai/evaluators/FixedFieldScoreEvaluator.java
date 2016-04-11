package othello.ai.evaluators;

import othello.Board;
import othello.Game;

import java.awt.*;

/**
 * Each piece on the board has a fixed value.
 * Returns the assigned value for the place of the move
 */
public class FixedFieldScoreEvaluator implements Evaluator {

    private static final int[] RATINGTHIRTY = 			{0, 7, 56, 63};
    private static final int[] RATING_TEN = 			{2, 5, 16, 23, 40, 47, 58, 61};
    private static final int[] RATING_FIVE = 			{3, 4, 18, 21, 24, 31, 32, 39, 42, 45, 59, 60};
    private static final int[] RATING_TWO = 			{19, 20, 26, 29, 34, 37, 43, 44};
    private static final int[] RATING_ONE = 			{10, 11, 12, 13, 17, 22, 25, 27, 28, 30, 33, 35, 36, 38, 41, 46, 50, 51, 52, 53};
    private static final int[] RATING_MINUS_TWENTYFIVE = {1, 6, 8, 9, 14, 15, 48, 49, 54, 55, 57, 62};
    private final Game game;

    public FixedFieldScoreEvaluator(Game game) {
        this.game = game;
    }

    @Override
    public int getScore(Board board, int side, Point move) {
        return getBoardPlaceRating(move);
    }

    private int getBoardPlaceRating(Point point){
        int place = game.pointToInt(point);
        if(placeOnBoardEquals(RATING_ONE, place))
            return 1;
        else if(placeOnBoardEquals(RATING_FIVE, place))
            return 5;
        else if(placeOnBoardEquals(RATING_MINUS_TWENTYFIVE, place))
            return -25;
        else if(placeOnBoardEquals(RATING_TWO, place))
            return 2;
        else if(placeOnBoardEquals(RATING_TEN, place))
            return 10;
        else if(placeOnBoardEquals(RATINGTHIRTY, place))
            return 30;
        return 0;
    }

    private boolean placeOnBoardEquals(int[] places, int place) {
        for (int i = 0; i < places.length; i++) {
            if(place == places[i])
                return true;
        }
        return false;
    }
}
