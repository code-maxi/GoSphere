package data;
import math.*;

public class GoPos {
    public static final double STONE_RAD_FAC = 0.3;
    public static final double STONE_DECREASE_FAC = 0.3;
    public static final int STONE_RES = 32;

    public final int x;
    public final int y;
    public final int n;
    public final int p;

    public GoPos(int x, int y, int n, int p) {
        this.n = n; this.p = p;
        int xx = (y == 0 || y == n/2) ? 0 : (x % n);
        int yy = y % n;
        if (yy > n/2) {
            yy = n - yy;
            xx = (n/2 + xx) % n;
        }
        this.x = xx;
        this.y = yy;
        if (this.x >= n) System.out.println(this);
    }

    public GoPos changeP(int pn) {
        return new GoPos(x, y, n, pn);
    }

    public GoVector[] to3D(double[][] wlist) { // 1. element = center, 2. = left corner of ellipse, 3. = right corner of ellipse
        double w1 = wlist[0][y];//2*Math.PI*y / n;
        double w2 = wlist[1][x];//2*Math.PI*x / n;
        GoVector center = GoVector.polar(w1, w2);

        double rad = Math.sqrt(center.com[0] * center.com[0] + center.com[1] * center.com[1]);
        double rad_f = (1 - GoPos.STONE_DECREASE_FAC) + GoPos.STONE_DECREASE_FAC * rad;

        double wa1 = 2*Math.PI/n * GoPos.STONE_RAD_FAC * rad_f;
        double wa2 = wa1 / rad * rad_f;

        GoVector[] res = new GoVector[GoPos.STONE_RES + 1];
        res[0] = center;

        for (int i = 0; i < GoPos.STONE_RES; i ++) {
            double a = (double) i / (double) GoPos.STONE_RES * 2 * Math.PI;
            res[i + 1] = GoVector.polar(
                w1 + Math.cos(a) * wa1,
                w2 + Math.sin(a) * wa2
            );
        }

        return res;
    }

    public String toString() {
        return "[" + x + " | " + y + " – n" + n + "]";
    }
}