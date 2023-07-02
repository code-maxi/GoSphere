package data;

import java.io.Serializable;

public class GoConfig implements Serializable {
    public final int n;
    public final double komi;
    public final int first_color;
    public final int advance_stones;
    public final String creator_name;
    public GoConfig(double komi, int n, int first_color, int advance_stones, String creator_name) {
        this.n = n;
        this.komi = komi;
        this.first_color = first_color;
        this.advance_stones = advance_stones;
        this.creator_name = creator_name;
    }
    public String toString() {
        return "GoConfig[n=" + n + ", komi=" + komi+ ", first_color=" + first_color + ", advance_stones=" + advance_stones + "]";
    }
}
