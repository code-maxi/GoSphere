package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class GoPosAbstract implements Serializable {
    public static int mod(int x, int n) {
        int xx = x;
        while (xx >= n) xx -= n;
        while (xx < 0) xx += n;
        return xx;
    }

    public final int x; // X-Coordinate
    public final int y; // Y-Coordinate
    public final int n; // Size of board
    public final int s; // Stone: 0 = nothing, 1 = black, 2 = white

    public GoPosAbstract(int x, int y, int n, int s) {
        this.n = n;
        this.s = s;
        this.x = newXY(x, y)[0];
        this.y = newXY(x, y)[1];
    }
    
    public int[] newXY(int x, int y) { return new int[]{x, y}; }
    
    public abstract ArrayList<GoPosAbstract> neighbours(GoStateAbstract state);
    public abstract GoPosAbstract changeStone(int s);

    public boolean equals(Object that) {
        return that instanceof GoPosAbstract && this.x == ((GoPosAbstract) that).x && this.y == ((GoPosAbstract) that).y;
    }

    public String posString() { return Arrays.toString(new int[] {x,y}); }

    public String toString() {
        return "[" + x + " | " + y + " – n" + n + ", " + s + "]";
    }
}
