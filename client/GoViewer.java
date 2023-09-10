package client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import data.*;
import server.GoServer;

public class GoViewer extends JFrame implements KeyListener, WindowListener {
    public final GoCanvas canvas;
    public final GoLabels labels;
    final GoClient client;

    public GoViewer(GoState state, GoClient client) {
        super();
        this.client = client;
        this.canvas = new GoCanvas(this);
        this.canvas.setState(state);
        this.labels = new GoLabels(state);
        setSize(800, 800);

        add(canvas, BorderLayout.CENTER);
        add(labels, BorderLayout.NORTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);
        addWindowListener(this);
        setLocationRelativeTo(null);
        setTitle("GoSphere – Game ["+(GoServer.FORMAT_ID(state.id))+"]");
        setAlwaysOnTop(true);
        setVisible(true);
    }
    public void setState(GoState state) {
        canvas.setState(state);
        labels.setState(state);
    }
    public void doMove(GoMove move) {
        if (client != null) client.send(move);
        //System.out.println("Client MOVE: " + move);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE) {
            client.send("NXT" + client.state.me);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void windowClosing​(WindowEvent e) {
        if (client != null) client.close();
    }

    @Override
    public void windowActivated​(WindowEvent e) {}

    @Override
    public void windowDeactivated​(WindowEvent e) {}

    @Override
    public void windowClosed​(WindowEvent e) {}

    @Override
    public void windowDeiconified​(WindowEvent e) {}

    @Override
    public void windowIconified​(WindowEvent e) {}

    @Override
    public void windowOpened​(WindowEvent e) {}
}