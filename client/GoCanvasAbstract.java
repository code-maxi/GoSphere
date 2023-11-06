package client;

import javax.swing.*;

import data.GoMove;
import data.GoPosAbstract;
import data.GoStateAbstract;
import math.GoMatrix;
import math.GoVector;

import java.awt.*;
import java.awt.event.*;
import java.util.TreeMap;

public abstract class GoCanvasAbstract extends JPanel implements ActionListener, MouseMotionListener, MouseListener, MouseWheelListener {
    public static final double SCROLL_FAC = 0.05;
    public static final double DRAG_FAC = 0.005;
    public static final double MAX_MOUSEMOVE = 5;

    private final GoViewer viewer;

    public GoStateAbstract state;

    protected double scale_state = 1;
    protected GoPosAbstract hover_pos;
    
    protected double[][] wlist = {{}, {}};
    
    protected GoMatrix Rot_old  = GoMatrix.unit();
    protected GoMatrix Rot_drag = GoMatrix.unit();
    protected double[][] drag_pos = null;

    protected GoMatrix Rot;
    protected GoMatrix Scl;
    protected GoMatrix Tsl;
    protected GoMatrix Trm;

    protected TreeMap<String, GoCanvasStoneAbstract> stones = new TreeMap<>();
    public void putStone(GoCanvasStoneAbstract stone) {
        stones.put(stone.getPos().posString(), stone);
    }

    public GoCanvasAbstract(GoViewer viewer) {
        super();
        this.viewer = viewer;
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    public abstract void setState(GoStateAbstract state);
    public abstract void paintMe(Graphics2D g2);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateMe();
        Graphics2D g2 = (Graphics2D) g;
        /*
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(
            RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        */
        paintMe(g2);
    }  

    // fitting object's size and position to window geometry
    protected synchronized void updateMe() {
        double[] size = { (double) getSize().width, (double) getSize().height };
        double scale = Math.min(size[0], size[1]) * 0.4 * scale_state;

        Rot = Rot_drag.connect(Rot_old);
        Scl = GoMatrix.scale(scale, -scale, scale);
        Tsl = GoMatrix.translate(new GoVector(size[0]/2, size[1]/2, 0d));

        Trm = Tsl.connect(Scl.connect(Rot));

        for (GoCanvasStoneAbstract p : stones.values()) { p.update(Trm); }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (drag_pos != null) {
            drag_pos[1][0] = e.getX();
            drag_pos[1][1] = e.getY();

            Rot_drag = GoMatrix.rotate(
                new GoVector(
                    (drag_pos[1][1] - drag_pos[0][1]) * GoCanvas.DRAG_FAC,
                    (drag_pos[1][0] - drag_pos[0][0]) * GoCanvas.DRAG_FAC,
                    0
                )
            );

            repaint(); 
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        hover_pos = null;
        boolean running = (state.status == GoStateAbstract.RUNNING_STATUS && state.me == state.turn);
        boolean delete = state.status == GoStateAbstract.DELETE_STATUS;
        if (running || delete) {
            for (GoCanvasStoneAbstract point : stones.values()) {
                if (point.isHovered(e.getX(), e.getY())) {
                    hover_pos = point.getPos().changeStone(state.me+1);
                    break;
                }
            }
        }
        repaint();
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {
        drag_pos = new double[2][2];
        drag_pos[0][0] = e.getX();
        drag_pos[0][1] = e.getY();
        drag_pos[1][0] = e.getX();
        drag_pos[1][1] = e.getY();
    }

    public void mouseReleased(MouseEvent e) {
        Rot_old  = Rot_drag.connect(Rot_old);
        Rot_drag = GoMatrix.unit();
        if (hover_pos != null) {
            GoVector delta = new GoVector(
                drag_pos[1][0] - drag_pos[0][0],
                drag_pos[1][1] - drag_pos[0][1],
                0
            );
            if (delta.length() <= MAX_MOUSEMOVE) {
                viewer.doMove(new GoMove(hover_pos, state.me));
            }
        }
    }

    public void actionPerformed(ActionEvent evt) {}

    public void mouseWheelMoved(MouseWheelEvent e) {
        scale_state += e.getWheelRotation() * GoCanvas.SCROLL_FAC * scale_state;
        repaint();
    }
}
