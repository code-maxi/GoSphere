package data;
import math.*;

public class GoPos {
    public static final double STONE_RAD_FAC = 0.8;

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
            xx += n/2;
        }
        this.x = xx;
        this.y = yy;
    }

    public GoPos changeP(int pn) {
        return new GoPos(x, y, n, pn);
    }

    public GoVector[] to3D() { // 1. element = center, 2. = left corner of ellipse, 3. = right corner of ellipse
        double wa = 2*Math.PI/n * GoPos.STONE_RAD_FAC * 0.2;
        double w1 = 2*Math.PI*y / n;
        double w2 = 2*Math.PI*x / n;
        return new GoVector[] {
            GoVector.polar(w1, w2),
            GoVector.polar(w1 + wa, w2 + wa),
            GoVector.polar(w1 - wa, w2 - wa)
        };
    }

    public String toString() {
        return "[" + x + " | " + y + " – n" + n + "]";
    }
}