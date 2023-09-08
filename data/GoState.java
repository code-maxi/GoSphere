package data;
import java.util.Arrays;

public class GoState extends GoStateAbstract {
    public GoState(
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

    public GoState(int n, int id) { super(n, id); }

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
        int[][] copied_stones = emptyArray(n);
        int[][] copied_colors = emptyArray(n);
        for (int i = 0; i < stones.length; i ++) {
            copied_stones[i] = Arrays.copyOf(stones[i], stones[i].length);
            copied_colors[i] = Arrays.copyOf(colors[i], colors[i].length);
        }
        return new GoState(n, id, copied_stones, copied_colors, copied_labels, me, turn, status);
    }
}