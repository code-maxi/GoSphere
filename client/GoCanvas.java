package client;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import data.*;
import java.util.ArrayList;
import math.*;

public class GoCanvas extends JPanel implements ActionListener, MouseMotionListener, MouseListener {
    private GoState state;
    private ArrayList<GoCanvasPoint> points = new ArrayList<GoCanvasPoint>();
    private double[] old_rotate = new double[3];
    private double drag_fac = 0.005;
    private double[][] drag_pos = null;

    public GoCanvas(GoState state) {
        super();
        addMouseListener(this);
        addMouseMotionListener(this);
        this.state = state;
        setState(state);
    }

    public void setState(GoState state) {
        this.state = state;
        points.clear();
        for (GoPos[] row : state.pieces) {
            for (GoPos pos : row) {
                points.add(new GoCanvasPoint(pos));
            }
        }
        repaint();
    }

    private void updateMe() {
        double[] size = { (double) getSize().width, (double) getSize().height };
        double scale = (size[0] < size[1] ? size[0] : size[1]) * 0.4;

        GoMatrix Rot = GoMatrix.rotate(drag_rotate());
        GoMatrix Scl = GoMatrix.scale(scale, -scale, scale);
        GoMatrix Tsl = GoMatrix.translate(new GoVector(size[0]/2, size[1]/2, 0d));

        GoMatrix Trm = Tsl.mul(Scl.mul(Rot));

        //System.out.println(size[0] + " " + size[1] + " Matrizen:");
        //System.out.println("Rot:\n"+ Rot + "\nScl:\n" + Scl + "\nTsl:\n" + Tsl + "\nTrm\n" + Trm + "______");

        for (GoCanvasPoint p : points) { p.update(Trm); }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateMe();

        Graphics2D g2 = (Graphics2D) g;
        for (GoCanvasPoint p : points) {
            Color color = p.color();
            if (color != null) {
                
                g2.setPaint(Color.RED);
                g2.fill(p.ellipse2);
            }
        }
    }

    private double[] drag_rotate() {
        double[] res = {0, 0, 0};
        if (drag_pos != null) {
            res = new double[]{
                old_rotate[0] + (drag_pos[1][1] - drag_pos[0][1]) * drag_fac,
                old_rotate[1] + (drag_pos[1][0] - drag_pos[0][0]) * drag_fac,
                old_rotate[2]
            };
        }
        return res;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (drag_pos != null) {
            drag_pos[1][0] = e.getX();
            drag_pos[1][1] = e.getY();
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
        old_rotate = drag_rotate();
        drag_pos = null;
    }

    public void actionPerformed(ActionEvent evt) {}
}