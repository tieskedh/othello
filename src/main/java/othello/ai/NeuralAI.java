package othello.ai;

import neuralnetwork.Network;
import othello.Board;
import othello.Game;

import java.awt.*;
import java.io.InputStream;

/**
 * Created by Laurens on 10-4-2016.
 */
public class NeuralAI {

    private int side;
    private int opponent;
    private Network network;
    private Game game;

    public NeuralAI(int side, int opponent, InputStream networkStream, Game game) {
        this.side = side;
        this.opponent = opponent;
        this.network = Network.fromFile(networkStream);
        this.game = game;
    }

    public int getNetworkMove(){
        Point[] possibleMoves = game.getBoard().getPossibleMoves(side);
        float[] ratings = getRatings(game.getBoard(),network);
        float max = -Float.MAX_VALUE;
        int move = 0;
        for (Point possibleMove : possibleMoves) {
            int index = pointToInt(possibleMove);
            if(ratings[index]>max){
                max = ratings[index];
                move = index;
            }
        }
        return move;
    }

    private float[] getRatings(Board board, Network network){
        int[][] pieces = board.getBoardPieces();
        int count = 0;
        float[] input = new float[128];
        for (int[] piece : pieces) {
            for (int i : piece) {
                input[i++] = side==i? 1f : -1f;
                input[i++] = side==opponent? 1f : -1f;
            }
        }
        return network.evaluate(input);
    }


    private int pointToInt(Point point) {
        return point.x * 8 + point.y;
    }

}
