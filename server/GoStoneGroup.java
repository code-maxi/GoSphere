package server;

import java.util.ArrayList;
import data.GoPosAbstract;

/*
 * 
 */
public class GoStoneGroup {
    public final ArrayList<GoPosAbstract> stones = new ArrayList<GoPosAbstract>();
    public final ArrayList<GoPosAbstract> liberties = new ArrayList<GoPosAbstract>();

    public GoStoneGroup fillNear(GoPosAbstract pos, int[][] data) { return fillNear(pos, data, new int[] { pos.s }); }

    public GoStoneGroup fillNear(GoPosAbstract pos, int[][] data, int[] colors) {
        if (!this.stones.contains(pos)) {
            stones.add(pos);
            ArrayList<GoPosAbstract> neighbours = pos.neighbours(data);
            for (GoPosAbstract n : neighbours) {
                if (n.s == 0 && !liberties.contains(n)) liberties.add(n);
                for (int c : colors) {
                    if (n.s == c && !this.stones.contains(n)) fillNear(n, data, colors);
                }
            }
        }
        return this;
    }

    public boolean containColor(int color) {
        for (GoPosAbstract stone : stones) {
            if (stone.s == color) return true;
        }
        return false;
    }
}
