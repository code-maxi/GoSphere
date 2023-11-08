package data;
import java.util.Arrays;

public class GoStateCube extends GoStateAbstract {
    public GoStateCube(
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

    public GoStateCube(int n, int id) { super(n, id); }

    @Override
    public int[][] emptyArray(int n) {
        return new int[n][6*n];
    }
    
    @Override
    public String stringArray(int[][] stones) {
        String res = "";
        for (int y = 0; y < n; y ++) {
            res += "\n  "+"  ".repeat(2*n);
            for (int x = 0; x < n; x ++) res += stones[y][5*n+x]+" ";
        }
        res += "\n";
        for (int y = 0; y < n; y ++) {
            res += "\n";
            for (int x = 0; x < 4*n; x ++) {
                if (x > 0 && x % n == 0) res += " ";
                res += stones[y][x]+" ";
            }
        }
        res += "\n";
        for (int y = 0; y < n; y ++) {
            res += "\n  "+"  ".repeat(2*n);
            for (int x = 0; x < n; x ++) {
                res += stones[y][4*n+x]+" ";
            }
        }
        return res;
    }

    @Override
    public int[][] cloneArray(int[][] stones) {
        int[][] arr = new int[n][6*n];
        for (int y = 0; y < stones.length; y ++) {
            for (int x = 0; x < stones[y].length; x ++) {
                arr[y][x] = stones[y][x];
            }
        }
        return arr;
    }

    @Override
    public GoStateAbstract copy(int me, int turn, int status) {
        String[] copied_labels = Arrays.copyOf(labels, labels.length);
        int[][][] copied_sc = copy_stones_colors();
        return new GoStateCube(n, id, copied_sc[0], copied_sc[1], copied_labels, me, turn, status);
    }

    @Override
    public GoPosAbstract posOnMe(int x, int y) {
        return new GoPosCube(x, y, stones.length, stones[y][x]);
    }
}