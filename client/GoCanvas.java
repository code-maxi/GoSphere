package client;
import java.awt.*;
import java.awt.geom.*;
import data.*;
import java.util.ArrayList;
import math.*;

public class GoCanvas extends GoCanvasAbstract {
    public static final Color[] SPHERE_COLORS = {
        new Color(0.8f, 0.8f, 0.8f, 0.9f),
        new Color(0.4f, 0.4f, 0.4f, 0.96f)
    };
    public static final float[] SPHERE_DIST = {0.45f, 1.0f};

    private ArrayList<GoCanvasStroke> strokes = new ArrayList<GoCanvasStroke>();

    public GoCanvas(GoState state, GoViewer viewer) { super(state, viewer); }

    @Override
    public void setState(GoState state) {
        this.state = state;
        this.hover_pos = null;
        
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
                GoPosAbstract pos = GoPos.goPosOnStones(j, i, state.stones); //new GoPos(j, i, state.stones);
                points.add(GoCanvasPoint.canvasPointOnSphere(pos, state.colors[j][i], wlist)); //new GoCanvasPoint(pos, state.colors[i][j], wlist));
            }
        }

        for (int y = 1; y < state.n/2; y ++) 
            strokes.add(new GoCanvasStroke(new int[]{0, y}, wlist));

        for (int x = 0; x < state.n; x ++) 
            strokes.add(new GoCanvasStroke(new int[]{1, x}, wlist));

        repaint();
    }

    @Override
    protected void updateMe() {
        super.updateMe();
        for (GoCanvasPoint p : points) { p.update(Trm); }
        for (GoCanvasStroke s : strokes) { s.update(Trm); }
    }

    private synchronized void paintSphere(Graphics2D g2) {
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
    public void paintMe(Graphics2D g2) {
        for (GoCanvasPoint p : points)   { p.paint(g2, 0, hover_pos); }
        paintSphere(g2);
        for (GoCanvasPoint p : points)   { p.paint(g2, 1, hover_pos); }
    }
}