package data;

import java.io.Serializable;

import math.GoColor;

import java.awt.Color;

public abstract class GoStateAbstract implements Serializable {
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final int RED = 3;
    public static final int BLUE = 4;
    public static final int GREEN = 5;

    public static final int WAIT_STATUS = 0;
    public static final int RUNNING_STATUS = 1;
    public static final int DELETE_STATUS = 2;
    public static final int END_STATUS = 3;

    public static GoColor color(int c) {
        Color res = null;
        if (c == BLACK) res = new Color(70, 70, 70, 255);
        if (c == WHITE) res = new Color(220, 220, 220, 255);
        if (c == RED) res = Color.RED;
        if (c == BLUE) res = Color.BLUE;
        if (c == GREEN) res = Color.GREEN;
        return res != null ? GoColor.convertAWT(res) : null;
    }

    public final int n;
    public final int id;
    public final int[][] stones; // 0: nothing, 1: black, 2: white
    public final int[][] colors; // 0: nothing, 1: black, 2: white
    public final String header;
    public final String[] labels;
    public final int me;
    public final int turn;
    public final int status;

    public String newHeader() {
        String header = "NO HEADER";
        if (this.me == 0 || this.me == 1) {
            if (this.status == WAIT_STATUS) header = "START STATE: The game has not begun yet. Press <Space> to start the game.";
            else if (this.status == RUNNING_STATUS) {
                if (this.turn == this.me) header = "RUNNING STATE: It is your turn, click on an intercept point or pass by pressing <Space>.";
                else header = "RUNNING STATE: Waiting for the other person's move...";
            }
            else if (this.status == DELETE_STATUS) header = "DELETE STATE: The game has ended. Please delete all dead stones by marking them and pressing <Space> to finish.";
            else if (this.status == END_STATUS) header = "END STATE: The stones have been deleted and the game is finished. You can start aq rematch by pressing <Space>.";
        }
        return header;
    }

    public GoStateAbstract(
        int n, 
        int id, 
        int[][] stones, 
        int[][] colors, 
        String[] labels, 
        int me, 
        int turn,
        int status
    ) {
        this.n = n;
        this.id = id;
        this.stones = stones;
        this.colors = colors;
        this.labels = labels;
        this.me = me;
        this.turn = turn;
        this.status = status;
        this.header = this.newHeader();
    }

    public GoStateAbstract(int n, int id) {
        this.n = n;
        this.id = id;
        this.stones = emptyArray(n);
        this.colors = emptyArray(n);
        this.labels = new String[2];
        this.me = -1;
        this.turn = -1;
        this.status = -1;
        this.header = this.newHeader();
    }

    public abstract int[][] emptyArray(int n);
    public abstract String stringArray(int[][] stones);
    public abstract int[][] cloneArray(int[][] stones);
    public abstract GoStateAbstract copy(int me, int turn, int status);

    public void clearArray(boolean clearStones, boolean clearColors) {
        for (int y = 0; y < stones.length; y ++) {
            for (int x = 0; x < stones[y].length; x ++) {
                if (clearStones) stones[y][x] = 0;
                if (clearColors) colors[y][x] = 0;
            }
        }
    }

    public void updateArray(int[][] newstones) {
        for (int y = 0; y < stones.length; y ++) {
            for (int x = 0; x < stones[y].length; x ++) {
                stones[y][x] = newstones[y][x];
            }
        }
    }

    public boolean equalsArray(int[][] otherstones) {
        for (int y = 0; y < stones.length; y ++) {
            for (int x = 0; x < stones[y].length; x ++) {
                if (stones[y][x] != otherstones[y][x]) return false;
            }
        }
        return true;
    }

    public String toString() {
        String resStones = stringArray(stones);
        String resColors = stringArray(colors);
        return "\nStones\n" + resStones + "\nColors\n" + resColors + "\nn="+n + ", id="+id + ", labels=["+labels[0]+","+labels[1]+"], turn="+turn+", me="+me;
    }
}
