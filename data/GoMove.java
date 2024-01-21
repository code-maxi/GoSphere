package data;

import java.io.Serializable;

public class GoMove implements Serializable {
    public final GoPosAbstract pos;
    public final int me;
    public final boolean tip;

    public GoMove(GoPosAbstract pos, int me, boolean tip) {
        this.pos = pos; this.me = me; this.tip = tip;
    }
    public String toString() { return "GoMove[pos="+pos+", me="+me+", tip="+tip+"]"; }
}
