package client;
import data.*;
import java.awt.*;
import java.awt.geom.*;

import math.*;

public class GoCanvasPoint {
    public GoPos pos;
    public Path2D.Double path;
    public Ellipse2D.Double ellipse;
    public GoVector center;
    
    public GoCanvasPoint(GoPos pos) {
        this.pos = pos;
    }

    public void update(GoMatrix Trm, double[][] wlist) {
        GoVector[] pos3d = this.pos.to3D(wlist);
        double[][] rendered_pos = new double[pos3d.length][pos3d[0].com.length];
        
        path = new Path2D.Double();
        for (int i = 0; i < pos3d.length; i ++) {
            GoVector trans = Trm.mul(pos3d[i]);
            rendered_pos[i] = trans.com;
            if (i == 0) center = trans;
            else {
                if (i == 1) path.moveTo(rendered_pos[i][0], rendered_pos[i][1]);
                else        path.lineTo(rendered_pos[i][0], rendered_pos[i][1]);
            }
        }
        path.closePath();
            
        ellipse = new Ellipse2D.Double(
            rendered_pos[0][0] - 5, 
            rendered_pos[0][1] - 5, 
            10, 10
        );
    }

    public void paint(Graphics2D g2) {
        Color col = color();
        if (col != null) {
            g2.setColor(col);
            g2.fill(path);
            if (pos.p == 1) {
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.GRAY);
                g2.draw(path);
            }
        }
    }

    public Color color() {
        return pos.p == 1 ? Color.WHITE : (pos.p == 2 ? new Color(0.1f, 0.1f, 0.1f) : null);
    }
}