package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import data.GoStateAbstract;

public class GoLabels extends JPanel implements Runnable {
    public static final Font[] LABEL_FONTS;
    public static final Border[] LABEL_BORDERS = {
            BorderFactory.createLineBorder(Color.BLACK, 2, true),
            BorderFactory.createLineBorder(Color.WHITE, 0, true)
    };
    public static final int ERROR_DELAY = 5000;

    static {
        float factor = (float) Toolkit.getDefaultToolkit().getScreenSize().width / 1920f;

        LABEL_FONTS = new Font[] {
                new Font(Font.MONOSPACED, Font.BOLD, (int) (20f * factor)),
                new Font(Font.MONOSPACED, Font.PLAIN, (int) (20f * factor)),
                new Font(Font.SANS_SERIF, Font.PLAIN, (int) (16f * factor))
        };
    }

    private final JLabel[] labels = { new JLabel(), new JLabel() };
    private final JLabel info_label;
    private GoStateAbstract state;

    public GoLabels(GoStateAbstract state) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        labels[0].setForeground(Color.BLACK);
        labels[1].setForeground(Color.GRAY);
        info_label = new JLabel("");
        info_label.setFont(LABEL_FONTS[2]);
        info_label.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        add(labels[0], BorderLayout.WEST);
        add(labels[1], BorderLayout.EAST);
        add(info_label, BorderLayout.SOUTH);
        setState(state);
    }

    public void run() {
        try {
            Thread.sleep(ERROR_DELAY);
        } catch (InterruptedException ex) {
        }
        updateInfo();
    }

    public void showError(String string) {
        info_label.setForeground(Color.RED);
        info_label.setText(string);
        new Thread(this).start();
    }

    public void updateInfo() {
        info_label.setForeground(Color.BLACK);
        info_label.setText(state.header);
    }

    public void setState(GoStateAbstract s) {
        this.state = s;
        updateInfo();
        for (int i = 0; i < labels.length; i++) {
            labels[i].setFont(
                    LABEL_FONTS[state.status == GoStateAbstract.RUNNING_STATUS ? (i == state.turn ? 0 : 1) : 0]);
            labels[i].setBorder(LABEL_BORDERS[i == state.turn ? 0 : 1]);
            labels[i].setText(" " + state.labels[i] + " ");
        }
    }
}
