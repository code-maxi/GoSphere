package data;

import java.io.Serializable;

public class GoMove implements Serializable {
    public final GoPosAbstract pos;
    public final int me;

    public GoMove(GoPosAbstract pos, int me) {
        this.pos = pos; this.me = me;
    }
    public String toString() { return "GoMove[pos="+pos+", me="+me+"]"; }
}
