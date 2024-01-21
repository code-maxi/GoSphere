package client;

import java.awt.*;
import java.awt.geom.*;
import math.*;

public class GoCanvasSphereStroke {
    public static final int STROKE_RES = 38 * 2;

    private GoVector[] circle = new GoVector[STROKE_RES];
    public Path2D.Double[] circle_path;

    public GoCanvasSphereStroke(int[] coord, double[][] wlist) {
        double w = wlist[coord[0]][coord[1]];
        for (int i = 0; i < STROKE_RES; i++) {
            double wi = (double) i / (double) GoCanvasSphereStroke.STROKE_RES * 2 * Math.PI;
            double[] polar = coord[0] == 0 ? new double[] { w, wi } : new double[] { wi, w };

            GoVector vec = GoVector.polar(polar);
            circle[i] = vec;
        }
    }

    public void update(GoMatrix Trm) {
        circle_path = new Path2D.Double[] {
                new Path2D.Double(),
                new Path2D.Double()
        };
        boolean[] paths_moved = { false, false };

        for (int i = 0; i <= STROKE_RES; i++) {
            double[] ren_pos = Trm.apply(circle[i % STROKE_RES]).com;

            int pi = ren_pos[2] < 0 ? 0 : 1;
            Path2D.Double path = circle_path[pi];
            if (paths_moved[pi]) {
                path.lineTo(ren_pos[0], ren_pos[1]);
            } else {
                path.moveTo(ren_pos[0], ren_pos[1]);
                paths_moved[pi] = true;
                paths_moved[(pi + 1) % 2] = false;
            }
        }
    }

    public void paint(Graphics2D g2, int layer) {
        if (circle_path != null && circle_path[layer] != null) {
            g2.setStroke(new BasicStroke(GoLabels.SCALE_FAC * 2f));
            g2.setPaint(Color.BLACK);
            g2.draw(circle_path[layer]);
        }
    }
}
