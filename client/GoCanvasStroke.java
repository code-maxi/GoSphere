package client;
import java.awt.*;
import java.awt.geom.*;
import math.*;

public class GoCanvasStroke {
    public static final int STROKE_RES = 32;

    private int[] coord;
    public Path2D.Double[] paths;

    public GoCanvasStroke(int[] coord) {
        this.coord = coord;
    }

    public void update(GoMatrix Trm, double[][] wlist) {
        double w = wlist[coord[0]][coord[1]];
        paths = new Path2D.Double[] {
            new Path2D.Double(),
            new Path2D.Double()
        };
        
        boolean[] paths_moved = {false, false};

        for (int i = 0; i <= GoCanvasStroke.STROKE_RES; i ++) {
            double wi = (double) i / (double) GoCanvasStroke.STROKE_RES * 2 * Math.PI;
            double[] w12 = coord[0] == 0 ? new double[]{w, wi} : new double[]{wi, w};
            
            GoVector ren_pos = Trm.mul(GoVector.polar(w12[0], w12[1]));
            
            int pi = ren_pos.com[2] < 0 ? 0 : 1;
            Path2D.Double path = paths[pi];
            if (paths_moved[pi]) {
                path.lineTo(ren_pos.com[0], ren_pos.com[1]);
            }
            else {
                path.moveTo(ren_pos.com[0], ren_pos.com[1]);
                paths_moved[pi] = true;
                paths_moved[(pi+1) % 2] = false;
            }
        }
    }

    public void paint(Graphics2D g2, int pi) {
        g2.setStroke(new BasicStroke(2));
        g2.setPaint(Color.BLACK);
        g2.draw(paths[pi]);
    }
}
