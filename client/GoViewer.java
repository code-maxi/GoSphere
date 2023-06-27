package client;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import data.*;

public class GoViewer extends JFrame {
    private GoCanvas canvas;
    public GoViewer(GoState state) {
        super();
        canvas = new GoCanvas(state);
    }
    public void init() {
        setSize(800, 800);
        add(canvas, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}