package client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import data.GoPosAbstract;
import data.GoStateAbstract;
import math.GoColor;
import math.GoMatrix;
import math.GoVector;

public class GoCanvasCubeFace implements GoCanvasStoneAbstract {
    public static final double PERSPECTIVE_Z_INFLUENCE = 0.3;

    public static final double STONE_POINTS_FAC = 0.7;
    public static final double MARK_POINTS_FAC = 0.4;
    
    public static final GoColor BASIC_FACE_COLOR = new GoColor(0.3, 0.3, 0.3, 0.85);

    public static final GoColor LIGHT_COLOR = new GoColor(1, 1, 1, 0.5);

    public static final GoColor[] STONE_COLORS = {
        new GoColor(0, 0, 0, 1),
        new GoColor(1, 1, 1, 1),
        new GoColor(0, 0, 0, 0.4),
        new GoColor(1, 1, 1, 0.4)
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
        GoMatrix.rotateY(Math.PI).connect(GoMatrix.rotateX(-Math.PI/2.0)),
        GoMatrix.rotateY(Math.PI).connect(GoMatrix.rotateX(Math.PI/2.0))
    };

    public static GoCanvasCubeFace createFace(GoPosAbstract pos, int color) {
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
        return new GoCanvasCubeFace(cornerPoints, pos, color);
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

    public GoCanvasCubeFace(GoVector[][] cornerPoints, GoPosAbstract pos, int color) {
        this.pos = pos;
        this.markcolor = GoStateAbstract.color(color);
        points[0] = cornerPoints[0];
        points[1] = cornerPoints[1];
        points[2] = cornerPoints[2];
        points[3][0] = GoMatrix.scale(1.0/((double)points[0].length)).apply(GoVector.addAll(points[0]));
    }

    @Override
    public GoPosAbstract getPos() { return pos; }
    
    @Override
    public void setPosColor(GoPosAbstract pos, GoColor color) { this.pos = pos; this.markcolor = color; }

    public double angle = 0;
    public int layer = 0;
    private double mixFac = 0;

    public void update(GoMatrix Trm, GoMatrix Rot) {
        for (int y = 0; y < points.length; y ++) {
            Path2D.Double path = new Path2D.Double();
            for (int x = 0; x < points[0].length; x ++) {
                if (points[y][x] != null) {
                    trans[y][x] = Rot.apply(points[y][x]);
                    double zoom = 1 + trans[y][x].com[2] * PERSPECTIVE_Z_INFLUENCE;
                    trans[y][x] = Trm.connect(GoMatrix.scale(zoom, zoom, 1)).apply(trans[y][x]);
                    GoVector rp = trans[y][x];
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
        layer = (int)(angle/Math.PI*2);
        mixFac = Math.abs(angle/Math.PI*2 - 1) * MAX_LIGHT_MIX;
    }
    

    private int stone = 0;

    @Override
    public void paint(Graphics2D g2, int l, GoPosAbstract hover) {
        if (layer == l) {
            boolean hovered = hover != null && hover.equals(pos);
            stone = hovered ? hover.s : pos.s;

            if (paths[0] != null) {
                g2.setStroke(new BasicStroke(1.5f));
                if (layer == 1) g2.setColor(Color.black); g2.draw(paths[0]);

                g2.setColor(BASIC_FACE_COLOR.mix(LIGHT_COLOR, mixFac).toAWT());
                g2.fill(paths[0]);

                if (layer == 0) g2.setColor(Color.black); g2.draw(paths[0]);
            }

            //if (layer == 0) debugPaint(g2, hovered);

            if (stone > 0 && paths[1] != null) {
                GoColor color = STONE_COLORS[stone-1 + (hovered ? 2 : 0)].mix(LIGHT_COLOR, mixFac*0.3);
                g2.setColor(color.toAWT()); g2.fill(paths[1]);
            }
            if (markcolor != null && paths[2] != null) {
                g2.setColor(markcolor.mix(LIGHT_COLOR, mixFac*0.5).toAWT());
                g2.setStroke(new BasicStroke(2));
                g2.draw(paths[2]);
            }
        }
    }

    private void debugPaint(Graphics2D g2, boolean hovered) {
        double size = 5.0;
        g2.setColor(Color.ORANGE);
        int cx = (int)trans[3][0].com[0];
        int cy = (int)trans[3][0].com[1];
        g2.fill(new Ellipse2D.Double(cx, cy, size, size));
        g2.setColor(layer == 0 ? Color.orange : Color.red);
        g2.drawString((pos.x/pos.n)+" "+pos, cx, cy);

        /*GoVector a = trans[0][0].to(trans[0][1]);
        GoVector b = trans[0][0].to(trans[0][trans[0].length-1]);
        GoVector n = b.crossProduct(a).unit().mul(50).add(trans[3][0]);
        g2.setColor(Color.green);
        g2.draw(new Line2D.Double(cx, cy, n.com[0], n.com[1]));
        g2.fill(new Ellipse2D.Double(n.com[0], n.com[1], size, size));*/
    }

    @Override
    public boolean isHovered(double x, double y) {
        return layer == 0 && paths[0].contains(x, y);
    }
}
