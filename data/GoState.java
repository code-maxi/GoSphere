package data;

public class GoState {
    public final int n;
    public final String id;
    public final GoPos[][] pieces; // 0: nothing, 1: black, 2: white

    public GoState(int n, String id, boolean random) {
        this.n = n;
        this.id = id;
        this.pieces = new GoPos[n][n];
        for (int i = 0; i < pieces.length; i ++) {
            for (int j = 0; j < pieces[i].length; j ++) {
                pieces[i][j] = new GoPos(i, j, n, random ? (int)(Math.random() > 0.8 ? (Math.random() * 2d + 1) : 0) : 0);
            }   
        }
    }

    public String toString() {
        String res = "";
        for (GoPos[] row : pieces) {
            res += "|  ";
            for (GoPos pos : row) { res += pos.p + "  "; }
            res += "|\n";
        }
        return res;
    }
}