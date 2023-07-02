package data;

import java.io.Serializable;

public class GoConfig implements Serializable {
    public final int n;
    public final int preferred_id;
    public final double komi;
    public final int first_color;
    public final int advance_stones;
    public final String creator_name;
    public GoConfig(double komi, int n, int preferred_id, int first_color, int advance_stones, String creator_name) {
        this.n = n;
        this.preferred_id = preferred_id;
        this.komi = komi;
        this.first_color = first_color;
        this.advance_stones = advance_stones;
        this.creator_name = creator_name;
    }
    public String toString() {
        return "GoConfig[n=" + n + ", preferred_id=" + preferred_id + ", komi=" + komi+ ", first_color=" + first_color + ", advance_stones=" + advance_stones + "]";
    }
}
