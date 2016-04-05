package gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * The gui for tictactoe
 * Created by Thijs de Haan on 24-3-2016.
 */
public class GameView extends JPanel implements ActionListener{

    /** the representation for the players by their name*/
    private final HashMap<Integer, String> players;
    /** the fields of the view*/
    private JButton[] buttons;
    private LinkedList<ActionListener> actionListeners = new LinkedList<>();
    private int size;


    /**
     * Creates the GameView for Othello
     * @param width the number of fields in the width
     * @param height the number of fields in the height
     * @param players the representation of the players by their id
     */
    public GameView(int width, int height, Map<Integer, String> players) {
        super(new GridLayout(width, height, 10, 10));
        size = width*height;
        this.players = new HashMap<>(players);
        buttons = new JButton[width*height];
        IntStream.range(0, size)
                .forEach(nr-> {
                    JButton button = new JButton();
                    button.setEnabled(false);
                    button.addActionListener(this::buttonPressed);
                    buttons[nr] = button;
                    add(button);
                });
        setVisible(true);
    }

    /**
     * Action to perform when a button is pressed
     * @param actionEvent the event which occurred
     */
    private void buttonPressed(ActionEvent actionEvent) {
        JButton button = (JButton) actionEvent.getSource();
        int nummer = IntStream.range(0, size)
                .filter(nr -> button==buttons[nr])
                .findFirst().getAsInt();
        fireEvent(new ActionEvent(this, nummer, "PRESSED"));
    }

    private void fireEvent(ActionEvent event) {
        actionListeners.forEach(listener -> listener.actionPerformed(event));
    }

    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        AbstractModel model = (AbstractModel) e.getSource();

        switch (e.getID()) {
            case AbstractModel.TURN_START:
                int[] validSets = model.getValidSets();
                Arrays.stream(validSets)
                        .forEach(
                                nr-> buttons[nr]
                                        .setEnabled(true)
                        );
                break;
            case AbstractModel.TURN_END:
                Arrays.stream(buttons)
                        .forEach(button->button.setEnabled(false));
                break;
            case AbstractModel.PLACE_PIECE:
                int place = model.getSetLocation();
                JButton button = buttons[place];
                button.setText(players.getOrDefault(model.getSide(), ""));
                button.setEnabled(false);
                break;
                default:
                    break;
        }
    }
}
