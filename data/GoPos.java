package data;
import java.io.Serializable;

import math.*;

public class GoPos implements Serializable {
    public final int x;
    public final int y;
    public final int n;
    public final int stone; // 0 = nothing, 1 = black, 2 = white

    private static int[] newXY(int x, int y, int n) {
        int xx = (y == 0 || y == n/2) ? 0 : (x % n);
        int yy = y % n;
        if (yy > n/2) {
            yy = n - yy;
            xx = (n/2 + xx) % n;
        }
        return new int[]{xx, yy};
    }

    public GoPos(int x, int y, int n, int s) {
        this.n = n; this.stone = s;
        int[] newxy = newXY(x, y, n);
        this.x = newxy[0]; this.y = newxy[1];
    }

    public GoPos(int x, int y, int[][] stones) {
        this.n = stones.length;
        int[] newxy = newXY(x, y, n);
        this.x = newxy[0]; this.y = newxy[1];
        this.stone = stones[y][x];
    }

    public GoPos changeStone(int s) { return new GoPos(x, y, n, s); }

    public boolean equals(GoPos that) { return this.x == that.x && this.y == that.y; }

    public String toString() {
        return "[" + x + " | " + y + " – n" + n + "]";
    }
}