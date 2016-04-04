import javax.swing.*;
import java.awt.*;

/**
 * Created by Gebruiker on 30/03/2016.
 */
public class main {
    public static void main(String[] args) {
        GameModule module = new GameModule("1","2");
        module.setClientBegins(false);
        module.start();
        JFrame frame = new JFrame("Othello");
        BorderLayout layout = new BorderLayout();
        frame.setLayout(layout);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(module.getView(), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        module.doPlayerMove("2", "37");

    }
}
