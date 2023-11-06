package client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.Arrays;

import data.GoPosAbstract;
import math.GoColor;
import math.GoMatrix;
import math.GoVector;

public class GoCanvasCubeFace implements GoCanvasStoneAbstract, Comparable<GoCanvasCubeFace> {
    public static final double PERSPECTIVE_Z_INFLUENCE = 0.001;
    public static final double STONE_POINTS_FAC = 0.8;
    public static final double MARK_POINTS_FAC = 0.6;
    public static final GoColor BASIC_FACE_COLOR = new GoColor(0.1, 0.1, 0.1, 0.7);
    public static final GoColor LIGHT_COLOR = new GoColor(1, 1, 1, 0.5);
    public static final GoColor[] STONE_COLORS = {
        new GoColor(0, 0, 0, 1),
        new GoColor(1, 1, 1, 1),
        new GoColor(0, 0, 0, 0.3),
        new GoColor(1, 1, 1, 0.3)
    };

    public static final double MAX_LIGHT_MIX = 0.5;

    public static final GoVector PERSPECTIVE_UNIT_VECTOR = new GoVector(0, 0, 1);

    public static final GoVector[] CUBE_SIDE_POINTS = {
        new GoVector(-0.5, -0.5, 0),
        new GoVector(0.5, -0.5, 0),
        new GoVector(0.5, 0.5, 0),
        new GoVector(-0.5, 0.5, 0)
    };

    public static final GoMatrix[] CUBE_SIDE_ROTATIONS = {
        GoMatrix.unit(),
        GoMatrix.rotateY(Math.PI/2.0*1),
        GoMatrix.rotateY(Math.PI/2.0*2),
        GoMatrix.rotateY(Math.PI/2.0*3),
        GoMatrix.rotateX(Math.PI/2.0),
        GoMatrix.rotateX(-Math.PI/2.0)
    };

    public static GoCanvasCubeFace createFace(GoPosAbstract pos) {
        double sidesize = 1.0/pos.n;
        GoVector origin = new GoVector(
            (pos.x % pos.n + 0.5 - (double)pos.n/2.0) * sidesize,
            (pos.y + 0.5 - (double)pos.n/2.0) * sidesize,
            0.5
        );

        GoMatrix M = CUBE_SIDE_ROTATIONS[pos.x/pos.n]
            .connect(GoMatrix.translate(origin)
            .connect(GoMatrix.scale(sidesize)));

        GoVector[][] cornerPoints = new GoVector[3][4];
        cornerPoints[0] = M.applyAll(CUBE_SIDE_POINTS);
        cornerPoints[1] = M.connect(GoMatrix.scale(STONE_POINTS_FAC)).applyAll(CUBE_SIDE_POINTS);
        cornerPoints[2] = M.connect(GoMatrix.scale(MARK_POINTS_FAC)).applyAll(CUBE_SIDE_POINTS);
        return new GoCanvasCubeFace(cornerPoints, pos);
    }

    /*
    * points[0] = basic points
    * points[1] = stone points
    * points[2] = mark points
    * points[3][0] = center
    */

    public GoPosAbstract pos;
    public GoColor markcolor;

    public GoVector[][] points = new GoVector[4][4];
    public GoVector[][] trans = new GoVector[4][4];
    public Path2D.Double[] paths = new Path2D.Double[4];

    public GoCanvasCubeFace(GoVector[][] cornerPoints, GoPosAbstract pos) {
        this.pos = pos;
        points[0] = cornerPoints[0];
        points[1] = cornerPoints[1];
        points[2] = cornerPoints[2];
        points[3][0] = GoMatrix.scale(1.0/((double)points[0].length)).apply(GoVector.addAll(points[0]));
    }

    @Override
    public GoPosAbstract getPos() { return pos; }
    
    @Override
    public void setPos(GoPosAbstract pos) { this.pos = pos; }

    @Override
    public void setColor(GoColor color) { this.markcolor = color; }

    public double angle = 0;
    private double mixFac = 0;
    private boolean inForeground = false;

    public void update(GoMatrix Trm) {
        for (int y = 0; y < points.length; y ++) {
            Path2D.Double path = new Path2D.Double();
            for (int x = 0; x < points[0].length; x ++) {
                if (points[y][x] != null) {
                    trans[y][x] = Trm.apply(points[y][x]);
                    double zoom = 1 + trans[y][x].com[2] * PERSPECTIVE_Z_INFLUENCE;
                    GoVector rp = GoMatrix.scale(zoom, zoom, 1).apply(trans[y][x]);
                    if (x == 0) path.moveTo(rp.com[0], rp.com[1]);
                    else        path.lineTo(rp.com[0], rp.com[1]);
                }
            }
            path.closePath();
            paths[y] = path;
        }
        GoVector a = trans[0][0].to(trans[0][1]);
        GoVector b = trans[0][0].to(trans[0][trans[0].length-1]);
        GoVector n = b.crossProduct(a).unit();
        angle = PERSPECTIVE_UNIT_VECTOR.angle(n);
        inForeground = angle < Math.PI/2;
        mixFac = Math.abs(angle/Math.PI*2 - 1) * MAX_LIGHT_MIX;

        /*System.out.println("Updating pos " + pos);
        System.out.println("Vector a " + a);
        System.out.println("Vector b " + b);
        System.out.println("Vector n " + n);
        System.out.println("angle " + angle);
        System.out.println("inforeground " + inForeground);
        System.out.println("mixfac " + mixFac);
        System.out.println();
        System.out.println();*/
    }

    @Override
    public int compareTo(GoCanvasCubeFace that) {
        // z coordinates of center
        return (int)((this.trans[3][0].com[2] - that.trans[3][0].com[2])*100000);
    }
    
    private GoColor mixLight(GoColor color) {
        return color.mix(LIGHT_COLOR, mixFac);
    }

    @Override
    public void paint(Graphics2D g2, int layer, GoPosAbstract hover) {
        boolean hovered = hover != null && hover.equals(pos);
        int stone = hovered ? hover.s : pos.s;

        g2.setColor(mixLight(BASIC_FACE_COLOR).toAWT()); g2.fill(paths[0]);
        g2.setStroke(new BasicStroke(2)); g2.setColor(Color.black); g2.draw(paths[0]);

        /*double size = 5.0;
        g2.setColor(Color.ORANGE);
        int cx = (int)transformedPoints[3][0].com[0];
        int cy = (int)transformedPoints[3][0].com[1];
        g2.fill(new Ellipse2D.Double(cx, cy, size, size));
        g2.setColor(inForeground ? Color.orange : Color.red);
        g2.drawString(""+(int)(mixFac*100), cx, cy);

        GoVector a = transformedPoints[0][0].to(transformedPoints[0][1]);
        GoVector b = transformedPoints[0][0].to(transformedPoints[0][transformedPoints[0].length-1]);
        GoVector n = b.crossProduct(a).unit().mul(50).add(transformedPoints[3][0]);
        g2.setColor(Color.green);
        g2.draw(new Line2D.Double(cx, cy, n.com[0], n.com[1]));
        g2.fill(new Ellipse2D.Double(n.com[0], n.com[1], size, size));*/
        /*g2.setColor(Color.blue);
        g2.draw(new Line2D.Double(transformedPoints[0][0].com[0], transformedPoints[0][0].com[1], transformedPoints[0][transformedPoints[0].length-1].com[0]+5, transformedPoints[0][transformedPoints[0].length-1].com[1]));*/

        if (stone > 0) {
            GoColor color = mixLight(STONE_COLORS[stone-1 + (hovered ? 2 : 0)]);
            g2.setColor(color.toAWT()); g2.fill(paths[1]);
        }
        if (markcolor != null) {
            g2.setColor(mixLight(markcolor).toAWT());
            g2.setStroke(new BasicStroke(2));
            g2.draw(paths[2]);
        }
    }

    @Override
    public boolean isHovered(double x, double y) {
        return inForeground && paths[0].contains(x, y);
    }
}
