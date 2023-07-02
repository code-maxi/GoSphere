package client;
import javax.swing.*;
import java.awt.*;
import data.*;
import server.GoServer;

public class GoViewer extends JFrame {
    private final GoCanvas canvas;
    private final GoLabels labels;
    final GoClient client;

    public GoViewer(GoState state, GoClient client) {
        super();
        this.client = client;
        this.canvas = new GoCanvas(state, this);
        this.labels = new GoLabels(state);
        setSize(800, 800);
        add(canvas, BorderLayout.CENTER);
        add(labels, BorderLayout.NORTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("GoSphere – Game ["+(GoServer.FORMAT_ID(state.id))+"]");
        setAlwaysOnTop(true);
        setVisible(true);
    }
    public void setState(GoState state) {
        canvas.setState(state);
        labels.setState(state);
    }
}