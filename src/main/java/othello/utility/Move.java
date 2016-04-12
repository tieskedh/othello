package othello.utility;

/**
 * Created by thijs on 9-4-2016.
 */

import java.awt.*;

/**
 * Internal class Move.
 * A Move object represents a single move from a player.
 * Contains the player doing the move, and the location for that move.
 */
public class Move {
    public static final int BOARD_SIZE = 8;
    public final int player;
    public final int location;

    /**
     * Constructor for Move class
     *
     * @param player
     * @param location
     */
    public Move(int player, int location) {
        this.player = player;
        this.location = location;
    }

    public Move(int player, Point location) {
        this.player = player;
        this.location = location.x * BOARD_SIZE + location.y;
    }

    public int getX() {
        return location/BOARD_SIZE;
    }

    public int getY() {
        return location%BOARD_SIZE;
    }


    /**
     * Converts data to String format for printing.
     *
     * @return
     */
    public String toString() {
        return "[ player: " + player +
                ", Location" + location + " ]";
    }

    public Point getPoint() {
        return new Point(getX(), getY());
    }
}

