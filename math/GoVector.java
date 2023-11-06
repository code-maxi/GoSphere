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

    public GoVector to(GoVector that) {
        return new GoVector(
            that.com[0] - this.com[0], 
            that.com[1] - this.com[1], 
            that.com[2] - this.com[2]
        );
    }

    public GoVector mul(double s) { return new GoVector(
        this.com[0]*s,
        this.com[1]*s,
        this.com[2]*s
    ); }

    public GoVector unit() { return mul(1/length()); }

    public static GoVector addAll(GoVector[] vectors) {
        GoVector result = new GoVector(0, 0, 0);
        for (GoVector v : vectors) result = result.add(v);
        return result;
    }

    public double scalarProduct(GoVector that) {
        double res = 0;
        for (int i = 0; i < com.length - 1; i ++) {
            res += this.com[i] * that.com[i];
        }
        return res;
    }

    public double angle(GoVector that) {
        double alpha = Math.acos(this.scalarProduct(that) / this.length() * that.length());
        return Math.min(alpha, Math.PI*2 - alpha);
    }

    public GoVector crossProduct(GoVector that) { return new GoVector(
        this.com[1]*that.com[2] - this.com[2]*that.com[1], 
        this.com[2]*that.com[0] - this.com[0]*that.com[2],
        this.com[0]*that.com[1] - this.com[1]*that.com[0] 
    ); }

    public double length() { return Math.sqrt(com[0] * com[0] + com[1] * com[1] + com[2] * com[2]); }

    public static GoVector polar(double[] polar) {
        return new GoVector(
            Math.sin(polar[0]) * Math.cos(polar[1]),
            Math.sin(polar[0]) * Math.sin(polar[1]),
            Math.cos(polar[0])
        );
    }

}