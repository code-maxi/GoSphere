package client;
import data.*;
import java.awt.geom.*;
import java.awt.Color;
import math.*;

public class GoCanvasPoint {
    public GoPos pos;
    public Ellipse2D.Double ellipse;
    public Ellipse2D.Double ellipse2;
    
    public GoCanvasPoint(GoPos pos) {
        this.pos = pos;
    }

    public void update(GoMatrix Trm) {
        GoVector[] pos3d = this.pos.to3D();
        
        double[] p0 = Trm.mul(pos3d[0]).com;
        double[] p1 = Trm.mul(pos3d[1]).com;
        double[] p2 = Trm.mul(pos3d[2]).com;

        double[] maxmin_x = p1[0] > p2[0] ? new double[]{p1[0], p2[0]} : new double[]{p2[0], p1[0]};
        double[] maxmin_y = p1[1] > p2[1] ? new double[]{p1[1], p2[1]} : new double[]{p2[1], p1[1]};

        ellipse = new Ellipse2D.Double(
            maxmin_x[1], maxmin_y[1],
            maxmin_x[0] - maxmin_x[1],
            maxmin_y[0] - maxmin_y[1]
        );
        ellipse2 = new Ellipse2D.Double(
            p0[0] - 5, p0[1] - 5, 10, 10
        );
    }

    public Color color() {
        return pos.p == 1 ? Color.WHITE : (pos.p == 2 ? Color.BLACK : Color.GREEN);
    }
}