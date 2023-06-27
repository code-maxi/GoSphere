package data;
import math.*;

public class GoPos {
    final int x;
    final int y;
    final int n;

    public GoPos(int x, int y, int n) {
        this.n = n;
        int xx = (y == 0 || y == n/2) ? 0 : (x % n);
        int yy = y % n;
        if (yy > n/2) {
            yy = n - yy;
            xx += n/2;
        }
        this.x = xx;
        this.y = yy;
    }

    public GoVector to3D(double r) {
        double w1 = 2*Math.PI*y / n;
        double w2 = 2*Math.PI*x / n;
        return new GoVector(
            r * Math.sin(w1) * Math.cos(w2),
            r * Math.sin(w1) * Math.sin(w2),
            r * Math.cos(w1)
        );
    }

    public String toString() {
        return "[" + x + " | " + y + " – n" + n + "]";
    }
}