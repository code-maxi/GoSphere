package math;
public class GoVector {
    public final double[] com;
    public GoVector() { com = new double[]{0, 0, 0, 1}; }
    public GoVector(double[] com) { this.com = com; }
    public GoVector(double x, double y, double z) { com = new double[]{x, y, z, 1}; }

    public String toString() {
        String res = "(  ";
        for (double c : com) { res += c + "  "; }
        res += ")";
        return res;
    }

    public GoVector add(GoVector other) { return new GoVector(
        this.com[0] + other.com[0],
        this.com[1] + other.com[1],
        this.com[2] + other.com[2]
    ); }

    public double length() { return Math.sqrt(com[0] * com[0] + com[1] * com[1] + com[2] * com[2]); }

    public static GoVector polar(double w1, double w2) {
        return new GoVector(
            Math.sin(w1) * Math.cos(w2),
            Math.sin(w1) * Math.sin(w2),
            Math.cos(w1)
        );
    }
}