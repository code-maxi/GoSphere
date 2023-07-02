package data;

import java.io.Serializable;
import java.util.Arrays;

public class GoState implements Serializable {
    public final int n;
    public final int id;
    
    public final int[][] stones; // 0: nothing, 1: black, 2: white
    public final String[] labels;
    public final int me;

    public int turn;

    public GoState(int n, int id, int[][] stones, String[] labels, int me, int turn) {
        this.n = n;
        this.id = id;
        this.stones = stones;
        this.labels = labels;
        this.turn = turn;
        this.me = me;
    }

    public GoState(int n, int id) {
        this(n, id, new int[n][n], new String[2], -1, -1);
        this.stones[0] = new int[1];
        this.stones[n/2-1] = new int[1];
    }

    public GoState copy(int me) {
        String[] copied_labels = Arrays.copyOf(labels, labels.length);
        int[][] copied_stones = new int[n][n];
        copied_stones[0] = new int[1];
        copied_stones[n/2-1] = new int[1];
        for (int i = 0; i < stones.length; i ++) {
            copied_stones[i] = Arrays.copyOf(stones[i], stones[i].length);
        }
        return new GoState(n, id, copied_stones, copied_labels, me, turn);
    }

    public String toString() {
        String res = "";
        for (int[] row : stones) {
            res += "|  ";
            for (int stone : row) { res += stone + "  "; }
            res += "|\n";
        }
        res += "n="+n + ", id="+id + ", labels=["+labels[0]+","+labels[1]+"], turn="+turn+", me="+me;
        return res;
    }
}