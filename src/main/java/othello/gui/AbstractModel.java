package othello.gui;

/**
 * This class defines the data the model needs to provide
 */
public abstract class AbstractModel {


    /**
     * All the sets where the current player can place a piece.
     * @return an array of points where the player can place their  piece
     */
    public abstract int[] getValidSets();

    /**
     * returns the point where the current piece is placed
     * @return the point of the current place
     */
    public abstract int getSetLocation();

    /**
     * returns the side of the player which is  currently placed
     * @return the current player
     */
    public abstract int getSide();


    /** eventID at the start of the game */
    public static final int TURN_START = 0;
    /** eventID at the end of the game */
    public static final int TURN_END = 1;
    /** eventID at the placement of a piece */
    public static final int PLACE_PIECE = 2;
}