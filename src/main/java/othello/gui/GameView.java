package othello.gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.IntStream;

/**
 * The othello.gui for tictactoe
 * Created by Thijs de Haan on 24-3-2016.
 */
public class GameView extends JPanel implements ActionListener {

    /**
     * the representation for the players by their name
     */
    private HashMap<Integer, Icon> players;
    /**
     * the fields of the view
     */
    private JButton[] buttons;
    private LinkedList<ActionListener> actionListeners = new LinkedList<>();
    private int size;


    /**
     * Creates the GameView for Othello
     *
     * @param width  the number of fields in the width
     * @param height the number of fields in the height
     */
    public GameView(int width, int height) {
        super(new GridLayout(width, height, 10, 10));
        setBackground(Color.BLACK);
        size = width * height;
        buttons = new JButton[width * height];

        // Creates, adds and disables buttons.
        IntStream.range(0, size)
                .forEach(nr -> {
                    JButton button = new JButton();
                    button.setFocusPainted(false);
                    setDisabled(button);
                    button.addActionListener(this::buttonPressed);
                    buttons[nr] = button;
                    add(button);
                });
        setVisible(true);
    }

    public void setPlayers(HashMap<Integer, Icon> players) {
        this.players = players;
    }

    /**
     * Action to perform when a button is pressed
     *
     * @param actionEvent the event which occurred
     */
    private void buttonPressed(ActionEvent actionEvent) {
        JButton button = (JButton) actionEvent.getSource();
        int nummer = IntStream.range(0, size)
                .filter(nr -> button == buttons[nr])
                .findFirst()
                .getAsInt();
        fireEvent(new ActionEvent(this, nummer, "PRESSED"));
    }

    private void fireEvent(ActionEvent event) {
        actionListeners.forEach(listener -> listener.actionPerformed(event));
    }

    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    /**
     * Disables button
     *
     * @param button
     */
    public void setDisabled(JButton button) {
        button.setEnabled(false);
        button.setBackground(new Color(0x37D622));
    }

    /**
     * Enables button
     *
     * @param button
     */
    public void setEnabled(JButton button) {
        button.setEnabled(true);
        button.setBackground(new Color(0x99FF00));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AbstractModel model = (AbstractModel) e.getSource();

        switch (e.getID()) {
            // At the start of the turn, enable the buttons on valid locations
            case AbstractModel.TURN_START:
                int[] validSets = model.getValidSets();
                Arrays.stream(validSets)
                        .mapToObj(nr -> buttons[nr])
                        .forEach(this::setEnabled);
                break;
            // At the end of the turn, disable all buttons.
            case AbstractModel.TURN_END:
                Arrays.stream(buttons)
                        .forEach(this::setDisabled);
                break;
            // places or flips a piece and disables the button that piece is on.
            case AbstractModel.PLACE_PIECE:
                int place = model.getSetLocation();
                JButton button = buttons[place];
                button.setIcon(players.getOrDefault(model.getSide(), null));
                button.setDisabledIcon(players.getOrDefault(model.getSide(), null));
                setDisabled(button);
                break;
            default:
                break;
        }
    }
}
