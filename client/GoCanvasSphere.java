package client;
import java.awt.*;
import java.awt.geom.*;
import data.*;
import java.util.ArrayList;
import math.*;

public class GoCanvasSphere extends GoCanvasAbstract {
    public static final Color[] SPHERE_COLORS = {
        new Color(0.8f, 0.8f, 0.8f, 0.9f),
        new Color(0.4f, 0.4f, 0.4f, 0.96f)
    };
    public static final float[] SPHERE_DIST = {0.45f, 1.0f};

    private ArrayList<GoCanvasSphereStroke> strokes = new ArrayList<GoCanvasSphereStroke>();

    public GoCanvasSphere(GoViewer viewer) { super(viewer); }

    @Override
    public synchronized void setState(GoStateAbstract state) {
        this.state = state;
        this.hover_pos = null;

        int[] wlistLength = { state.n/2 + 1, state.n + 1 };
        /*System.out.println("WLIST SIZE 0 " + wlist[0].length + " vs. " + wlistLength[0]);
        System.out.println("WLIST SIZE 1 " + wlist[1].length + " vs. " + wlistLength[1]);*/
        if (wlist[0].length != wlistLength[0] || wlist[1].length != wlistLength[1]) {
            this.wlist = new double[][] {
                new double[wlistLength[0]],
                new double[wlistLength[1]]
            };

            for (int y = 0; y < wlist[0].length; y ++) {
                if (y == 0 || y == wlist[0].length - 1) wlist[0][y] = 2*Math.PI*y / this.state.n;
                else wlist[0][y] = 2*Math.PI*(y+1) / (this.state.n+4);
            }
            for (int x = 0; x < wlist[1].length; x ++) {
                wlist[1][x] = 2*Math.PI*x / this.state.n;
            }
        }

        //int strokeSize = state.n/2-1 + state.n;
        //System.out.println("STROKE SIZE " + strokes.size() + " vs. " + strokeSize);
        if (strokes.size() != state.n/2 + state.n) {
            strokes.clear();
            for (int y = 1; y < state.n/2; y ++) 
                strokes.add(new GoCanvasSphereStroke(new int[]{0, y}, wlist));

            for (int x = 0; x < state.n; x ++) 
                strokes.add(new GoCanvasSphereStroke(new int[]{1, x}, wlist));
        }
        
        int pointsSize = (state.stones.length-2)*state.stones[1].length + 2;
        //System.out.println("POINTS SIZE " + points.size() + " vs. " + pointsSize);
        if (stones.size() != pointsSize) stones.clear();
        for (int y = 0; y < state.stones.length; y ++) {
            for (int x = 0; x < state.stones[y].length; x ++) {
                GoPosAbstract pos = state.posOnMe(x, y);
                if (stones.size() != pointsSize) {
                    putStone(GoCanvasSpherePoint.canvasPointOnSphere(pos, state.colors[y][x], wlist));
                }
                else {
                    for (GoCanvasStoneAbstract point : stones.values()) {
                        if (point.getPos().equals(pos)) {
                            point.setPos(pos);
                            point.setColor(GoStateAbstract.color(state.colors[y][x]));
                            break;
                        }
                    }
                }
            }
        }

        repaint();
    }

    @Override
    protected synchronized void updateMe() {
        super.updateMe();
        for (GoCanvasSphereStroke s : strokes) { s.update(Trm.connect(Rot)); } // UPDATED!
    }

    private synchronized void paintSphere(Graphics2D g2) {
        // Paint Kugel
        for (GoCanvasSphereStroke s : strokes) { s.paint(g2, 0); }

        GoVector p1 = Tsl.connect(Scl).apply(new GoVector(-1, 1, 0));
        GoVector p2 = Tsl.connect(Scl).apply(new GoVector(1, -1, 0));
        GoVector p3 = Tsl.connect(Scl).apply(new GoVector(0, 0, 0));

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
        g2.setPaint(new RadialGradientPaint(center, radius, GoCanvasSphere.SPHERE_DIST, GoCanvasSphere.SPHERE_COLORS));

        g2.fill(new Ellipse2D.Double(dim[0], dim[1], dim[2], dim[3]));

        for (GoCanvasSphereStroke s : strokes) { s.paint(g2, 1); }
    }

    @Override
    public void paintMe(Graphics2D g2) {
        for (GoCanvasStoneAbstract p : stones.values()) { p.paint(g2, 0, hover_pos); }
        paintSphere(g2);
        for (GoCanvasStoneAbstract p : stones.values()) { p.paint(g2, 1, hover_pos); }
    }
}