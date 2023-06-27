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

    public static GoVector polar(double w1, double w2) {
        return new GoVector(
            Math.sin(w1) * Math.cos(w2),
            Math.sin(w1) * Math.sin(w2),
            Math.cos(w1)
        );
    }
}