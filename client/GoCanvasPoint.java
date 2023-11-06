package client;
import data.*;
import java.awt.*;
import java.awt.geom.*;

import math.*;

public class GoCanvasPoint implements GoCanvasStoneAbstract {
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
            GoVector kar = Rot.apply(ka);
            vectors[i] = kar;
        }
        return vectors;
    }

    public static GoCanvasPoint canvasPointOnSphere(GoPosAbstract pos, int intcolor, double[][] wlist) {
        GoColor color = GoState.color(intcolor);
        double[] center_polar = {
            wlist[0][pos.y], 
            wlist[1][pos.x]
        };
        GoVector center = GoVector.polar(center_polar);
        GoVector[] stoneCircle = circleOnSphere(center_polar, Math.PI*STONE_RAD_FAC/pos.n,  STONE_RES);
        GoVector[] markedCircle = circleOnSphere(center_polar, Math.PI*STONE_RAD_FAC2/pos.n, STONE_RES);
        return new GoCanvasPoint(pos, color, center, stoneCircle, markedCircle);
    }
    
    public GoPosAbstract pos;
    public GoColor color;

    public final GoVector center;
    private final GoVector[] stoneCircle;
    private final GoVector[] markedCircle;
    
    public GoVector renderedCenter;
    
    public Ellipse2D.Double center_ellipse;
    public Path2D.Double stoneCircle_path;
    public Path2D.Double markedCircle_path;
    
    public GoCanvasPoint(
        GoPosAbstract pos, 
        GoColor color, 
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

    public GoPosAbstract getPos() { return pos; }
    public void setPos(GoPosAbstract pos) { this.pos = pos; }
    public void setColor(GoColor color) { this.color = color; }

    private void updatePath(Path2D.Double path, GoVector[] circ, GoMatrix Trm) {
        for (int i = 0; i < circ.length; i ++) {
            GoVector renpos = Trm.apply(circ[i]);
            if (i == 0) path.moveTo(renpos.com[0], renpos.com[1]);
            else        path.lineTo(renpos.com[0], renpos.com[1]);
        }
        path.closePath();
    }

    public void update(GoMatrix Trm) {
        stoneCircle_path = new Path2D.Double(); updatePath(stoneCircle_path,  stoneCircle,  Trm);
        if (color != null) {
            markedCircle_path = new Path2D.Double();
            updatePath(markedCircle_path, markedCircle, Trm);
        }
        renderedCenter = Trm.apply(center);
        center_ellipse = new Ellipse2D.Double(
            renderedCenter.com[0] - 5, 
            renderedCenter.com[1] - 5, 
            10, 10
        );
    }

    public void paint(Graphics2D g2, int layer, GoPosAbstract hoverpos) { // layer: 0 = behind sphere, 1 = in front of sphere
        boolean is_behind = renderedCenter.com[2] < 0;
        if ((is_behind && layer == 0) || (!is_behind && layer == 1)) {
            boolean hovered = hoverpos != null && hoverpos.equals(pos);
            paintStone(g2, hovered ? hoverpos.s : pos.s, hovered);
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
        }
        if (color != null) {
            g2.setColor(color.toAWT());
            g2.setStroke(new BasicStroke(2));
            g2.draw(markedCircle_path);
        }
    }

    public boolean isHovered(double x, double y) {
        return renderedCenter.com[2] > 0 && stoneCircle_path.contains(x, y);
    }
}