package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import data.*;
import server.GoServer;

public class GoViewer extends JFrame implements KeyListener, WindowListener {
    public final GoCanvasAbstract canvas;
    public final GoLabels labels;
    public GoStateAbstract state;
    final GoClient client;

    public GoViewer(GoStateAbstract state, GoClient client) {
        super();
        this.client = client;
        this.canvas = state instanceof GoStateCube ? new GoCanvasCube(this) : new GoCanvasSphere(this);
        this.state = state;
        this.canvas.setState(state);
        this.labels = new GoLabels(state);
        setSize(800, 800);

        add(labels, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);

        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/res/icon.png")));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);
        addWindowListener(this);
        setLocationRelativeTo(null);
        setTitle("Go3D – Game [" + (GoServer.FORMAT_ID(state.id)) + "]");
        setAlwaysOnTop(true);
        setVisible(true);
    }

    public void setState(GoStateAbstract state) {
        this.state = state;
        canvas.setState(state);
        labels.setState(state);
    }

    public void doMove(GoMove move) {
        if (client != null)
            client.send(move);

        /*
         * GoStateAbstract newState = state.copy((state.me+1)%2, (state.me+1)%2,
         * state.status);
         * newState.clearArray(false, true);
         * newState.stones[move.pos.y][move.pos.x] = move.me+1;
         * System.out.println("Move: " + move.pos);
         * for (GoPosAbstract n : move.pos.neighbours(state.stones)) {
         * System.out.println(n);
         * newState.colors[n.y][n.x] = GoStateAbstract.RED;
         * }
         * System.out.println(newState);
         * setState(newState);
         */

    }

    public void chatMessage(String text) {
        if (client != null)
            client.send("CHT" + text);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            client.send("NXT" + client.state.me);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_C && client != null) {
            canvas.showChatField();
        }
    }

    @Override
    public void windowClosing​(WindowEvent e) {
        if (client != null)
            client.close();
    }

    @Override
    public void windowActivated​(WindowEvent e) {
    }

    @Override
    public void windowDeactivated​(WindowEvent e) {
    }

    @Override
    public void windowClosed​(WindowEvent e) {
    }

    @Override
    public void windowDeiconified​(WindowEvent e) {
    }

    @Override
    public void windowIconified​(WindowEvent e) {
    }

    @Override
    public void windowOpened​(WindowEvent e) {
    }
}