import javax.swing.*;
import java.awt.*;

/**
 * Created by Gebruiker on 30/03/2016.
 */
public class main {
    public static void main(String[] args) {
        OthelloGameModule module = new OthelloGameModule("1", "2");
        module.start();
        JFrame frame = new JFrame("Othello");
        BorderLayout layout = new BorderLayout();
        frame.setLayout(layout);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(module.getView(), BorderLayout.CENTER);
        JButton button = new JButton("swap");
        button.addActionListener(e -> {
            module.nextPlayer();
            module.board.turnEnd();
            module.board.turnStart();
        });
        frame.add(button,BorderLayout.NORTH);
        frame.pack();
        frame.setVisible(true);

    }
}
