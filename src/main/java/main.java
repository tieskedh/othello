import javax.swing.*;

/**
 * Created by Gebruiker on 30/03/2016.
 */
public class main {
    public static void main(String[] args) {
        OthelloGameModule module = new OthelloGameModule("1", "2");
        module.start();
        JFrame frame = new JFrame("Othello");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(module.getView());
        frame.pack();
        frame.setVisible(true);

    }
}
