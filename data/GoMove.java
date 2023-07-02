package data;

import java.io.Serializable;

public class GoMove implements Serializable {
    public static final int POS = 0;
    public static final int PASS = 1;
    public static final int GIVEUP = 2;

    public final int action;
    public final GoPos pos;
    public final int me;

    public GoMove(int action, GoPos pos, int me) {
        this.action = action; this.pos = pos; this.me = me;
    }
    public String toString() { return "GoMove[action="+action+", pos="+pos+", me="+me+"]"; }
}
