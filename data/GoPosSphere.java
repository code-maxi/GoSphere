package data;
import java.util.ArrayList;

public class GoPosSphere extends GoPosAbstract {
    public GoPosSphere(int x, int y, int n, int s) {
        super(x, y, n, s);
    }

    @Override
    public int[] newXY(int x, int y) {
        int xx = (y == 0 || y == n/2) ? 0 : mod(x, n);
        int yy = mod(y, n);
        if (yy > n/2) {
            yy = n - yy;
            xx = mod(n/2 + xx, n);
        }
        return new int[]{xx, yy};
    }

    @Override
    public GoPosAbstract changeStone(int s) { return new GoPosSphere(x, y, n, s); }

    @Override
    public ArrayList<GoPosAbstract> neighbours(GoStateAbstract state) {
        ArrayList<GoPosAbstract> neighbours = new ArrayList<GoPosAbstract>();
        if (y == 0 || y == n/2) {
            int ny = y == 0 ? 1 : n/2-1;
            for (int nx = 0; nx < state.stones[ny].length; nx ++) {
                neighbours.add(state.posOnMe(nx, ny));
            }
        }
        else {
            neighbours.add(state.posOnMe(x, y-1));
            neighbours.add(state.posOnMe(x+1, y));
            neighbours.add(state.posOnMe(x, y+1));
            neighbours.add(state.posOnMe(x-1, y));
        }
        return neighbours;
    }
}