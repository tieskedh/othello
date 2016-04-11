package othello.ai;

import othello.Board;
import othello.Game;
import othello.utility.WeightedMove;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by thijs on 9-4-2016.
 */
public interface AI {
    public String getMove();
}
