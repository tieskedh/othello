import othello.GameModule;

import javax.swing.*;
import java.awt.*;

/**
 * Main class. Only used for testing ATM, as this program is otherwise launched through the framework.
 */
public class main {
    public static void main(String[] args) {
        GameModule module = new GameModule("1", "2");
        module.setClientBegins(true);
        module.setClientPlayPiece("White");
        module.start();
        JFrame frame = new JFrame("Othello");
        BorderLayout layout = new BorderLayout();
        frame.setLayout(layout);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(module.getView(), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        module.doPlayerMove("1", "37");

    }
}
