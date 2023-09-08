package data;
import java.util.ArrayList;

public class GoPos extends GoPosAbstract {
    public static GoPosAbstract goPosOnStones(int x, int y, int[][] stones) {
        GoPosAbstract pos = new GoPos(x, y, stones[1].length, 0);
        pos = pos.changeStone(stones[pos.y][pos.x]);
        return pos;
    }

    public GoPos(int x, int y, int n, int s) {
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
    public GoPosAbstract changeStone(int s) { return new GoPos(x, y, n, s); }

    @Override
    public ArrayList<GoPosAbstract> neighbours(int[][] stones) {
        ArrayList<GoPosAbstract> neighbours = new ArrayList<GoPosAbstract>();
        if (y == 0 || y == n/2) {
            int ny = y == 0 ? 1 : n/2-1;
            int[] circles = stones[y];
            for (int nx = 0; nx < circles.length; nx ++) {
                neighbours.add(goPosOnStones(nx, ny, stones));
            }
        }
        else {
            neighbours.add(goPosOnStones(x, y-1, stones));
            neighbours.add(goPosOnStones(x+1, y, stones));
            neighbours.add(goPosOnStones(x, y+1, stones));
            neighbours.add(goPosOnStones(x-1, y, stones));
        }
        return neighbours;
    }
}