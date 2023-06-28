package client;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import data.*;
import java.util.ArrayList;
import math.*;

public class GoCanvas extends JPanel implements ActionListener, MouseMotionListener, MouseListener, MouseWheelListener {
    public static final Color[] SPHERE_COLORS = {
        new Color(0.8f, 0.8f, 0.8f, 0.9f),
        new Color(0.4f, 0.4f, 0.4f, 0.96f)
    };
    public static final float[] SPHERE_DIST = {0.45f, 1.0f};
    public static final double SCROLL_FAC = 0.05;
    
    private GoState state;
    
    private ArrayList<GoCanvasPoint> points = new ArrayList<GoCanvasPoint>();
    private ArrayList<GoCanvasStroke> strokes = new ArrayList<GoCanvasStroke>();

    private double drag_fac = 0.005;
    private double[][] drag_pos = null;
    private double scale_state = 1;

    private double[][] wlist;

    private GoMatrix Rot_old  = GoMatrix.unit();
    private GoMatrix Rot_drag = GoMatrix.unit();

    private GoMatrix Rot;
    private GoMatrix Scl;
    private GoMatrix Tsl;

    private GoMatrix Trm;

    public GoCanvas(GoState state) {
        super();
        this.state = state;
        this.wlist = new double[][] {
            new double[state.n/2 + 1],
            new double[state.n + 1]
        };

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        setState(state);
    }

    public void setState(GoState state) {
        this.state = state;
        points.clear();
        strokes.clear();

        for (GoPos[] row : state.pieces) {
            for (GoPos pos : row) points.add(new GoCanvasPoint(pos));
        }

        for (int y = 1; y < state.n/2; y ++) 
            strokes.add(new GoCanvasStroke(new int[]{0, y}));

        for (int x = 0; x < state.n; x ++) 
            strokes.add(new GoCanvasStroke(new int[]{1, x}));

        repaint();
    }

    private void updateMe() {
        double[] size = { (double) getSize().width, (double) getSize().height };
        double scale = (size[0] < size[1] ? size[0] : size[1]) * 0.4 * scale_state;

        Rot = Rot_drag.mul(Rot_old);

        Scl = GoMatrix.scale(scale, -scale, scale);
        Tsl = GoMatrix.translate(new GoVector(size[0]/2, size[1]/2, 0d));

        Trm = Tsl.mul(Scl.mul(Rot));

        for (int y = 0; y < wlist[0].length; y ++) {
            if (y == 0 || y == wlist[0].length - 1) wlist[0][y] = 2*Math.PI*y / this.state.n;
            else wlist[0][y] = 2*Math.PI*(y+1) / (this.state.n+4);
        }
        for (int x = 0; x < wlist[1].length; x ++) {
            wlist[1][x] = 2*Math.PI*x / this.state.n;
        }

        //System.out.println(size[0] + " " + size[1] + " Matrizen:");
        //System.out.println("Rot:\n"+ Rot + "\nScl:\n" + Scl + "\nTsl:\n" + Tsl + "\nTrm\n" + Trm + "______");

        for (GoCanvasPoint p : points) { p.update(Trm, wlist); }
        for (GoCanvasStroke s : strokes) { s.update(Trm, wlist); }
    }

    private void paintSphere(Graphics2D g2) {
        // Paint Kugel
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
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateMe();

        Graphics2D g2 = (Graphics2D) g;

        for (GoCanvasStroke s : strokes) { s.paint(g2, 0); }
        for (GoCanvasPoint p : points) { if (p.center.com[2] < 0) p.paint(g2); }

        paintSphere(g2);

        for (GoCanvasStroke s : strokes) { s.paint(g2, 1); }
        for (GoCanvasPoint p : points) { if (p.center.com[2] > 0) p.paint(g2); }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (drag_pos != null) {
            drag_pos[1][0] = e.getX();
            drag_pos[1][1] = e.getY();

            Rot_drag = GoMatrix.rotate(
                new GoVector(
                    (drag_pos[1][1] - drag_pos[0][1]) * drag_fac,
                    (drag_pos[1][0] - drag_pos[0][0]) * drag_fac,
                    0
                )
            );

            repaint(); 
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

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
    }

    public void actionPerformed(ActionEvent evt) {}

    public void mouseWheelMoved(MouseWheelEvent e) {
        scale_state += e.getWheelRotation() * GoCanvas.SCROLL_FAC * scale_state;
        repaint();
    }
}