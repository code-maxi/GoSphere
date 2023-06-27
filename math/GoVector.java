package math;
public class GoVector {
    final double[] com;
    public GoVector() { com = new double[]{0, 0, 0, 1}; }
    public GoVector(double[] com) { this.com = com; }
    public GoVector(double x, double y, double z) { com = new double[]{x, y, z, 1}; }

    public String toString() {
        String res = "(  ";
        for (double c : com) { res += c + "  "; }
        res += ")";
        return res;
    }
}