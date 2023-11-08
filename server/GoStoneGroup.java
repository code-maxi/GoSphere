package server;

import java.util.ArrayList;
import data.GoPosAbstract;
import data.GoStateAbstract;

/*
 * 
 */
public class GoStoneGroup {
    public final ArrayList<GoPosAbstract> stones = new ArrayList<GoPosAbstract>();
    public final ArrayList<GoPosAbstract> liberties = new ArrayList<GoPosAbstract>();

    public GoStoneGroup fill(GoPosAbstract pos, GoStateAbstract state) {
        return fill(pos, state, new int[] { pos.s });
    }

    public GoStoneGroup fill(GoPosAbstract pos, GoStateAbstract state, int[] colors) {
        if (!this.stones.contains(pos)) {
            stones.add(pos);
            ArrayList<GoPosAbstract> neighbours = pos.neighbours(state);
            for (GoPosAbstract neighbour : neighbours) {
                if (neighbour.s == 0 && !liberties.contains(neighbour)) {
                    liberties.add(neighbour);
                }
                for (int color : colors) {
                    if (neighbour.s == color) {
                        fill(neighbour, state, colors);
                    }
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
