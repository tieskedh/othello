package othello;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;

/**
 * Created by thijs on 3-4-2016.
 */
public class Board {
    /**
     * represents a space where no piece is placed
     */
    private static final int EMPTY = 0;
    /**
     * represents a space with a piece of the client
     */
    private static final int PLAYER_1 = 1;
    /**
     * represents a space with the piece of the opposing player
     */
    private static final int PLAYER_2 = 2;
    /**
     * The size of a side of the board
     */
    private final int BOARD_SIZE;
    private int board[][];
    private Game game;


    /**
     * Creates a board by the given size
     *
     * @param boardSize the size of a side of the board
     */
    public Board(int boardSize, Game game) {
        this.game = game;
        BOARD_SIZE = boardSize;
        board = new int[BOARD_SIZE][BOARD_SIZE];
    }

    public Board(Game game, int[][] board) {
        this.board = board;
        this.game = game;
        BOARD_SIZE = board.length;
    }

    /**
     * place a move on the board
     *
     * @param location the location where to place the move
     * @param player   the player which places the move
     * @return the move is successfully executed
     */
    public boolean doMove(Point location, int player) {
        if (isValidMove(location, player)) {
            doMoveInternal(location, player);
            return true;
        } else {
            return false;
        }
    }

    /**
     * The real execution of the move.<p />
     * Warning this methode does not check if it is possible to do the move
     *
     * @param location the location where to place the move
     * @param player   the player which places the move
     * @see #doMove(Point, int)
     */
    private void doMoveInternal(Point location, int player) {
        setAtLocation(location, player);
        IntStream.range(0, 9)
                .mapToObj(nr -> checkLinePieces(location, nr / 3 - 1, nr % 3 - 1, player, new ArrayList<>()))
                .flatMap(Collection::stream)
                .distinct()
                .forEach(this::flipPiece);
    }

    /**
     * Returns the value of the given location
     *
     * @param location the value where to get the value from
     * @return the value of the field specified by location
     */
    private int getAtLocation(Point location) {
        return board[location.x][location.y];
    }

    /**
     * Sets the value of the given location
     *
     * @param location The location where to set the value from
     * @param player   the player which needs to be placed at the given location
     */
    private void setAtLocation(Point location, int player) {
        board[location.x][location.y] = player;
        game.piecePlaced(location, player);
    }

    /**
     * Flips the side of a piece.
     * If no Piece is placed, keeps the field untouched
     *
     * @param location the location to flip
     */
    private void flipPiece(Point location) {
        if (getAtLocation(location) == 0) return;
        setAtLocation(location, 3 - getAtLocation(location));
    }

    /**
     * Counts the occurrence of the given player on the board
     *
     * @param player the player to count the pieces from
     * @return the occurrence of the player
     */
    public int getOccurrences(int player) {
        return (int) Arrays.stream(board)
                .flatMapToInt(Arrays::stream)
                .filter(val -> val == player)
                .count();
    }

    /**
     * Clears the board
     */
    public void clear() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
    }

    /**
     * Prepares a standard 8 by 8 game.
     */
    public void prepareStandardGame() {
        clear();
        setAtLocation(new Point(3, 3), PLAYER_2);
        setAtLocation(new Point(4, 3), PLAYER_1);
        setAtLocation(new Point(3, 4), PLAYER_1);
        setAtLocation(new Point(4, 4), PLAYER_2);

    }

    /**
     * Prepares a 4 by 4 game for testing
     * todo remove this method after testing
     */
    public void prepareTestGame() {
        setAtLocation(new Point(1, 1), PLAYER_1);
        setAtLocation(new Point(2, 1), PLAYER_2);
        setAtLocation(new Point(1, 2), PLAYER_2);
        setAtLocation(new Point(2, 2), PLAYER_1);
    }

    /**
     * Returns the pieces which needs to flip in a direction.
     *
     * @param location   the location where the first piece is placed.
     * @param offsetX    the change in one step in the x-direction
     * @param offsetY    the change in one step in the y-direction
     * @param player     the player which places his move
     * @param piecesList the list which already needs to flip in the direction
     * @return the list with locations which needs to flip
     */
    private ArrayList<Point> checkLinePieces(Point location, int offsetX, int offsetY, int player, ArrayList<Point> piecesList) {

        Point testPoint = new Point(location);

        testPoint.move(testPoint.x + offsetX, testPoint.y + offsetY);
        //If not piece or out of bounds and empty
        if (PointOutOfBounds(testPoint) || getAtLocation(testPoint) == 0 || (offsetX == 0 && offsetY == 0)) {
            return new ArrayList<>();
        }
        //If it encounters another piece of the same player, end.
        if (getAtLocation(testPoint) == player) {
            return piecesList;
        } else {
            //if it encounters a piece of the opposite player, add to fliplist.
            piecesList.add(testPoint);
            //get new location and go recursive
            return checkLinePieces(testPoint, offsetX, offsetY, player, piecesList);
        }
    }

    /**
     * checks if a location is out of bounds
     *
     * @param location the location to check
     * @return the location is outside the board
     */
    private boolean PointOutOfBounds(Point location) {
        return location.x < 0 || location.x > BOARD_SIZE - 1 ||
                location.y < 0 || location.y > BOARD_SIZE - 1;
    }

    /**
     * Returns the possible moves of a player
     *
     * @param player the player to get the moves from
     * @return the possible moves
     */
    public Point[] getPossibleMoves(int player) {

        Point[] test = IntStream.range(0, BOARD_SIZE * BOARD_SIZE)
                .mapToObj(nr -> new Point(nr / BOARD_SIZE, nr % BOARD_SIZE))
                .filter(location -> isValidMove(location, player))
                .toArray(Point[]::new);
        return test;
    }

    /**
     * Checks if the move can be placed at the given place.
     * This method is already executed at the placement of a move
     *
     * @param location the location of the move to check
     * @param player   the player to set the move to check
     * @return the move is possible
     * @see #doMove(Point, int)
     */
    public boolean isValidMove(Point location, int player) {
        if (getAtLocation(location) != 0) {
            return false;
        }
        return !IntStream.range(0, 9)
                .mapToObj(nr -> checkLinePieces(location, nr / 3 - 1, nr % 3 - 1, player, new ArrayList<>()))
                .allMatch(ArrayList::isEmpty);
    }

    public int getSize() {
        return BOARD_SIZE;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[0].length; y++) {
                sb.append("\t");
                sb.append(board[x][y]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Sets up the board for the AI player
     *
     * @param boardPieces
     */
    public void setBoardPieces(int[][] boardPieces) {
        int[][] newBoardPieces = new int[8][8];
        for (int i = 0; i < boardPieces.length; i++) {
            for (int j = 0; j < boardPieces[i].length; j++) {
                newBoardPieces[i][j] = boardPieces[i][j];
            }
        }
        this.board = Arrays.copyOf(newBoardPieces, newBoardPieces.length);
    }

    public int[][] getBoardPieces() {
        return this.board;
    }
}