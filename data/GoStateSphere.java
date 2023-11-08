package data;
import java.util.Arrays;

public class GoStateSphere extends GoStateAbstract {
    public GoStateSphere(
        int n, 
        int id, 
        int[][] stones, 
        int[][] colors, 
        String[] labels, 
        int me, 
        int turn,
        int status
    ) {
        super(n, id, stones, colors, labels, me, turn, status);
    }

    public GoStateSphere(int n, int id) { super(n, id); }

    public int[][] emptyArray(int n) {
        int[][] arr = new int[n/2+1][n];
        arr[0] = new int[1];
        arr[n/2] = new int[1];
        return arr;
    }
    
    public String stringArray(int[][] stones) {
        String res = "";
        for (int[] row : stones) {
            res += "|  ";
            for (int stone : row) { res += stone + "  "; }
            res += "|\n";
        }
        return res;
    }

    public int[][] cloneArray(int[][] stones) {
        int n = stones[1].length;
        int[][] arr = new int[n/2+1][n];
        arr[0] = new int[1];
        arr[n/2] = new int[1];
        for (int y = 0; y < stones.length; y ++) {
            for (int x = 0; x < stones[y].length; x ++) {
                arr[y][x] = stones[y][x];
            }
        }
        return arr;
    }

    public GoStateAbstract copy(int me, int turn, int status) {
        String[] copied_labels = Arrays.copyOf(labels, labels.length);
        int[][][] copied_sc = copy_stones_colors();
        return new GoStateSphere(n, id, copied_sc[0], copied_sc[1], copied_labels, me, turn, status);
    }

    @Override
    public GoPosAbstract posOnMe(int x, int y) {
        GoPosAbstract pos = new GoPosSphere(x, y, stones[1].length, 0);
        pos = pos.changeStone(stones[pos.y][pos.x]);
        return pos;
    }
}