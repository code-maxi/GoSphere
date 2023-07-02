package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.GoState;

public class GoLabels extends JPanel implements Runnable {
    public static final String[] INFO_TEXT = {
        "The game has not begun yet. Waiting for an opponent...",
        "It's your turn, click on an intercept point or pass by pressing <Ctrl+P>.",
        "Waiting for the other person's move..."
    };

    public static final Font[] LABEL_FONTS = {
        new Font(Font.MONOSPACED, Font.BOLD, 17),
        new Font(Font.MONOSPACED, Font.PLAIN, 17)
    };
    public static final int ERROR_DELAY = 5000;
    
    private final JLabel[] labels = { new JLabel(), new JLabel() };
    private final JLabel info_label;
    private GoState state;

    public GoLabels(GoState state) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        labels[0].setForeground(Color.BLACK);
        labels[1].setForeground(Color.GRAY);
        info_label = new JLabel("");
        info_label.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        info_label.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        add(labels[0], BorderLayout.WEST);
        add(labels[1], BorderLayout.EAST);
        add(info_label, BorderLayout.SOUTH);
        setState(state);
    }

    public void run() {
        try { Thread.sleep(ERROR_DELAY); }
        catch(InterruptedException ex) {}
        updateInfo();
    }

    public void showError(String string) {
        info_label.setForeground(Color.RED);
        info_label.setText(string);
        new Thread(this).start();
    }

    public void updateInfo() {
        info_label.setForeground(Color.BLACK);
        info_label.setText(INFO_TEXT[state.turn == -1 ? 0 : (state.turn == state.me ? 1 : 2)]);
    }

    public void setState(GoState s) {
        this.state = s;
        updateInfo();
        for (int i = 0; i < labels.length; i ++) {
            labels[i].setFont(LABEL_FONTS[i == state.turn ? 0 : 1]);
            labels[i].setText(state.labels[i]);
        }
    }
}
