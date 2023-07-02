package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.GoState;

public class GoLabels extends JPanel {
    public static final Font[] LABEL_FONTS = {
        new Font(Font.MONOSPACED, Font.BOLD, 17),
        new Font(Font.MONOSPACED, Font.PLAIN, 17)
    };
    private final JLabel[] labels = { new JLabel(), new JLabel() };

    public GoLabels(GoState state) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        labels[0].setForeground(Color.BLACK);
        labels[1].setForeground(Color.GRAY);
        add(labels[0], BorderLayout.WEST);
        add(labels[1], BorderLayout.EAST);
        setState(state);
    }

    public void setState(GoState state) {
        for (int i = 0; i < labels.length; i ++) {
            labels[i].setFont(LABEL_FONTS[i == state.turn ? 0 : 1]);
            labels[i].setText(state.labels[i]);
        }
    }
}
