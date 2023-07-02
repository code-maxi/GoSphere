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
    public static final double STONE_RAD_FAC2 = 0.4;
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

    public GoPos pos;
    public GoVector center;
    public GoVector ren_center;
    
    public Ellipse2D.Double ellipse;
    public Path2D.Double circle_path;

    private GoVector[] circle;
    private GoVector[] circle2; // last active
    
    public GoCanvasPoint(GoPos pos, double[][] wlist) {
        this.pos = pos;
        
        double[] center_polar = {
            wlist[0][pos.y], 
            wlist[1][pos.x]
        };
        center = GoVector.polar(center_polar);
        
        circle = circleOnSphere(center_polar, Math.PI*STONE_RAD_FAC/pos.n,  STONE_RES);
        //circle2 = circleOnSphere(center_polar, Math.PI*STONE_RAD_FAC2, STONE_RES);
    }

    public void update(GoMatrix Trm) {        
        circle_path = new Path2D.Double();
        for (int i = 0; i < circle.length; i ++) {
            GoVector ren_pos = Trm.mul(circle[i]);
            if (i == 0) circle_path.moveTo(ren_pos.com[0], ren_pos.com[1]);
            else        circle_path.lineTo(ren_pos.com[0], ren_pos.com[1]);
        }
        circle_path.closePath();

        ren_center = Trm.mul(center);
            
        ellipse = new Ellipse2D.Double(
            ren_center.com[0] - 5, 
            ren_center.com[1] - 5, 
            10, 10
        );
    }

    public void paint(Graphics2D g2, int layer, GoPos hover) { // layer: 0 = behind sphere, 1 = in front of sphere
        boolean is_behind = ren_center.com[2] < 0;
        if ((is_behind && layer == 0) || (!is_behind && layer == 1)) {
            boolean hovered = hover != null && hover.equals(pos);
            paintStone(g2, hovered ? hover.stone : pos.stone, hovered);
        }
    }

    private void paintStone(Graphics2D g2, int stone, boolean hovered) {
        if (stone > 0) {
            Color color = GoCanvasPoint.STONE_COLORS[stone-1 + (hovered ? 2 : 0)];
            g2.setColor(color); g2.fill(circle_path);

            if (stone == 2) {
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.GRAY); g2.draw(circle_path);
            }

            //g2.setColor(Color.RED); g2.fill(ellipse);
        }
        /*else {
            Color color = new Color(1f, 1f, 0f, 0.5f);
            g2.setColor(color); g2.fill(circle_path);
        }*/
    }

    public boolean isHovered(double x, double y) {
        return ren_center.com[2] > 0 && circle_path.contains(x, y);
    }
}