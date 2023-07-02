package client;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import data.*;
import java.util.ArrayList;
import math.*;

public class GoCanvas extends JPanel implements ActionListener, MouseMotionListener, MouseListener, MouseWheelListener, KeyListener {
    public static final Color[] SPHERE_COLORS = {
        new Color(0.8f, 0.8f, 0.8f, 0.9f),
        new Color(0.4f, 0.4f, 0.4f, 0.96f)
    };
    public static final float[] SPHERE_DIST = {0.45f, 1.0f};
    public static final double SCROLL_FAC = 0.05;
    public static final double DRAG_FAC = 0.005;
    public static final double MAX_MOUSEMOVE = 10;

    private GoState state;
    
    private ArrayList<GoCanvasPoint> points = new ArrayList<GoCanvasPoint>();
    private ArrayList<GoCanvasStroke> strokes = new ArrayList<GoCanvasStroke>();
    
    private double scale_state = 1;
    private GoPos hover_pos;
    
    private double[][] wlist;
    
    private GoMatrix Rot_old  = GoMatrix.unit();
    private GoMatrix Rot_drag = GoMatrix.unit();
    private double[][] drag_pos = null;

    private GoMatrix Rot;
    private GoMatrix Scl;
    private GoMatrix Tsl;
    private GoMatrix Trm;

    private final GoViewer viewer;

    public GoCanvas(GoState state, GoViewer viewer) {
        super();
        this.state = state;
        this.viewer = viewer;

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);
        setState(state);
    }

    public void setState(GoState state) {
        this.state = state;
        
        this.wlist = new double[][] {
            new double[state.n/2 + 1],
            new double[state.n + 1]
        };

        for (int y = 0; y < wlist[0].length; y ++) {
            if (y == 0 || y == wlist[0].length - 1) wlist[0][y] = 2*Math.PI*y / this.state.n;
            else wlist[0][y] = 2*Math.PI*(y+1) / (this.state.n+4);
        }
        for (int x = 0; x < wlist[1].length; x ++) {
            wlist[1][x] = 2*Math.PI*x / this.state.n;
        }

        points.clear();
        strokes.clear();

        for (int i = 0; i < state.stones.length; i ++) {
            for (int j = 0; j < state.stones[i].length; j ++) {
                GoPos pos = new GoPos(j, i, state.stones);
                points.add(new GoCanvasPoint(pos, wlist));
            }
        }

        for (int y = 1; y < state.n/2; y ++) 
            strokes.add(new GoCanvasStroke(new int[]{0, y}, wlist));

        for (int x = 0; x < state.n; x ++) 
            strokes.add(new GoCanvasStroke(new int[]{1, x}, wlist));

        repaint();
    }

    private void updateMe() {
        double[] size = { (double) getSize().width, (double) getSize().height };
        double scale = (size[0] < size[1] ? size[0] : size[1]) * 0.4 * scale_state;

        Rot = Rot_drag.mul(Rot_old);
        Scl = GoMatrix.scale(scale, -scale, scale);
        Tsl = GoMatrix.translate(new GoVector(size[0]/2, size[1]/2, 0d));

        Trm = Tsl.mul(Scl.mul(Rot));

        for (GoCanvasPoint p : points) { p.update(Trm); }
        for (GoCanvasStroke s : strokes) { s.update(Trm); }
    }

    private void paintSphere(Graphics2D g2) {
        // Paint Kugel
        for (GoCanvasStroke s : strokes) { s.paint(g2, 0); }

        GoVector p1 = Tsl.mul(Scl).mul(new GoVector(-1, 1, 0));
        GoVector p2 = Tsl.mul(Scl).mul(new GoVector(1, -1, 0));
        GoVector p3 = Tsl.mul(Scl).mul(new GoVector(0, 0, 0));

        float[] dim = {
            (float) p1.com[0],
            (float) p1.com[1],
            (float) (p2.com[0] - p1.com[0]), 
            (float) (p2.com[1] - p1.com[1]),
            (float) p3.com[0],
            (float) p3.com[1]
        };

        Point2D center = new Point2D.Float(dim[4], dim[5]);
        float radius = dim[2] / 2;
        g2.setPaint(new RadialGradientPaint(center, radius, GoCanvas.SPHERE_DIST, GoCanvas.SPHERE_COLORS));

        g2.fill(new Ellipse2D.Double(dim[0], dim[1], dim[2], dim[3]));

        for (GoCanvasStroke s : strokes) { s.paint(g2, 1); }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateMe();

        Graphics2D g2 = (Graphics2D) g;

        for (GoCanvasPoint p : points)   { p.paint(g2, 0, hover_pos); }
        paintSphere(g2);
        for (GoCanvasPoint p : points)   { p.paint(g2, 1, hover_pos); }
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

    public void doMove(GoMove move) {
        if (viewer.client != null) viewer.client.send(move);
        System.out.println("Client MOVE: " + move);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        hover_pos = null;
        for (GoCanvasPoint point : points) {
            if (state.turn == state.me && point.isHovered(e.getX(), e.getY()) && point.pos.stone == 0) {
                hover_pos = point.pos.changeStone(state.me+1); // TODO
                break;
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
    }

    public void mouseReleased(MouseEvent e) {
        Rot_old  = Rot_drag.mul(Rot_old);
        Rot_drag = GoMatrix.unit();
        GoVector delta = new GoVector(
            drag_pos[1][0] - drag_pos[0][0],
            drag_pos[1][1] - drag_pos[0][1],
            0
        );
        if (delta.length() <= MAX_MOUSEMOVE) {
            doMove(new GoMove(GoMove.POS, hover_pos, state.me));
        }
    }

    public void actionPerformed(ActionEvent evt) {}

    public void mouseWheelMoved(MouseWheelEvent e) {
        scale_state += e.getWheelRotation() * GoCanvas.SCROLL_FAC * scale_state;
        repaint();
    }

    
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_P && e.isControlDown()) {
            doMove(new GoMove(GoMove.PASS, null, state.me));
        }
    }
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}