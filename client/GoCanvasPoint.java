package client;
import data.*;
import java.awt.*;
import java.awt.geom.*;

import math.*;

public class GoCanvasPoint {
    public static final Color[] STONE_COLORS = {
        new Color(0.1f, 0.1f, 0.1f, 1f),  // black
        new Color(1.0f, 1.0f, 1.0f, 1f),  // white
        new Color(0.1f, 0.1f, 0.1f, 0.3f), // black hover
        new Color(1.0f, 1.0f, 1.0f, 0.3f)  // white hover
    };
    public static final double STONE_RAD_FAC = 0.5;
    public static final double STONE_RAD_FAC2 = 0.35;
    public static final double STONE_DECREASE_FAC = 0.3;
    public static final int STONE_RES = 32;

    public static GoVector[] circleOnSphere(double[] polar, double r, int res) {
        GoMatrix Rot = GoMatrix.rotate(new GoVector(polar[0], 0, polar[1] + Math.PI/2));
        GoVector[] vectors = new GoVector[res];
        double length = Math.sqrt(1 - r*r);
        for (int i = 0; i < res; i ++) {
            double a = (double) i / (double) res * Math.PI * 2;
            GoVector ka = new GoVector(
                Math.cos(a) * r,
                Math.sin(a) * r,
                length
            );
            GoVector kar = Rot.mul(ka);
            vectors[i] = kar;
        }
        return vectors;
    }

    public static GoCanvasPoint canvasPointOnSphere(GoPosAbstract pos, int intcolor, double[][] wlist) {
        Color color = GoState.color(intcolor);
        double[] center_polar = {
            wlist[0][pos.y], 
            wlist[1][pos.x]
        };
        GoVector center = GoVector.polar(center_polar);
        GoVector[] stoneCircle = circleOnSphere(center_polar, Math.PI*STONE_RAD_FAC/pos.n,  STONE_RES);
        GoVector[] markedCircle = color != null ? circleOnSphere(center_polar, Math.PI*STONE_RAD_FAC2/pos.n, STONE_RES) : null;
        return new GoCanvasPoint(pos, color, center, stoneCircle, markedCircle);
    }
    
    public final GoPosAbstract pos;
    private final Color color;

    public final GoVector center;
    private final GoVector[] stoneCircle;
    private final GoVector[] markedCircle;
    
    public GoVector renderedCenter;
    
    public Ellipse2D.Double center_ellipse;
    public Path2D.Double stoneCircle_path;
    public Path2D.Double markedCircle_path;
    
    public GoCanvasPoint(
        GoPosAbstract pos, 
        Color color, 
        GoVector center,
        GoVector[] stoneCircle, 
        GoVector[] markedCircle
    ) {
        this.pos = pos;
        this.color = color;
        this.center = center;
        this.stoneCircle = stoneCircle;
        this.markedCircle = markedCircle;
    }

    private void updatePath(Path2D.Double path, GoVector[] circ, GoMatrix Trm) {
        for (int i = 0; i < circ.length; i ++) {
            GoVector renpos = Trm.mul(circ[i]);
            if (i == 0) path.moveTo(renpos.com[0], renpos.com[1]);
            else        path.lineTo(renpos.com[0], renpos.com[1]);
        }
        path.closePath();
    }

    public void update(GoMatrix Trm) {
        stoneCircle_path  = new Path2D.Double(); updatePath(stoneCircle_path,  stoneCircle,  Trm);
        if (color != null) { markedCircle_path = new Path2D.Double(); updatePath(markedCircle_path, markedCircle, Trm); }
        renderedCenter = Trm.mul(center);
        center_ellipse = new Ellipse2D.Double(
            renderedCenter.com[0] - 5, 
            renderedCenter.com[1] - 5, 
            10, 10
        );
    }

    public void paint(Graphics2D g2, int layer, GoPosAbstract hover) { // layer: 0 = behind sphere, 1 = in front of sphere
        boolean is_behind = renderedCenter.com[2] < 0;
        if ((is_behind && layer == 0) || (!is_behind && layer == 1)) {
            boolean hovered = hover != null && hover.equals(pos);
            paintStone(g2, hovered ? hover.s : pos.s, hovered);
        }
    }

    private void paintStone(Graphics2D g2, int stone, boolean hovered) {
        if (stone > 0) {
            Color color = GoCanvasPoint.STONE_COLORS[stone-1 + (hovered ? 2 : 0)];
            g2.setColor(color); g2.fill(stoneCircle_path);

            if (stone == 2) {
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.GRAY); g2.draw(stoneCircle_path);
            }
            //g2.setColor(Color.RED); g2.fill(ellipse);
        }
        if (color != null) {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2));
            g2.draw(markedCircle_path);
        }
    }

    public boolean isHovered(double x, double y) {
        return renderedCenter.com[2] > 0 && stoneCircle_path.contains(x, y);
    }
}