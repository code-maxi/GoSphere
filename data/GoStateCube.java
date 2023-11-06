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

    public int[][] emptyArray(int n) {
        return new int[n][6*n];
    }
    
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

    public int[][] cloneArray(int[][] stones) {
        int[][] arr = new int[n][6*n];
        for (int y = 0; y < stones.length; y ++) {
            for (int x = 0; x < stones[y].length; x ++) {
                arr[y][x] = stones[y][x];
            }
        }
        return arr;
    }

    public GoStateAbstract copy(int me, int turn, int status) {
        String[] copied_labels = Arrays.copyOf(labels, labels.length);
        int[][] copied_stones = emptyArray(n);
        int[][] copied_colors = emptyArray(n);
        for (int i = 0; i < stones.length; i ++) {
            copied_stones[i] = Arrays.copyOf(stones[i], stones[i].length);
            copied_colors[i] = Arrays.copyOf(colors[i], colors[i].length);
        }
        return new GoStateCube(n, id, copied_stones, copied_colors, copied_labels, me, turn, status);
    }
}