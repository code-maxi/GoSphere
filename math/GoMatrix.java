package math;

public class GoMatrix {
    public final double[][] com;
    public GoMatrix(double[][] com) {
        this.com = com;
    }
    public GoVector mul(GoVector vec) {
        GoVector vec2 = null;
        if (vec.com.length == this.com[0].length) {
            vec2 = new GoVector();
            int n = this.com[0].length;

            for (int i = 0; i < this.com.length; i ++) {
                double yi = 0;
                for (int j = 0; j < n; j ++) {
                    yi += this.com[i][j] * vec.com[j];
                }
                vec2.com[i] = yi;
            }
        }
        return vec2;
    }

    public GoMatrix mul(GoMatrix A) {
        double[][] firstMatrix = this.com;
        double[][] secondMatrix = A.com;
        double[][] result = new double[firstMatrix.length][secondMatrix[0].length];

        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[row].length; col++) {
                double cell = 0;
                for (int i = 0; i < secondMatrix.length; i++) {
                    cell += firstMatrix[row][i] * secondMatrix[i][col];
                }
                result[row][col] = cell;
            }
        }
        return new GoMatrix(result);
    }

    public String toString() {
        String res = "";
        for (double[] row : this.com) {
            res += "|  ";
            for (double c : row) { res += c + "  "; }
            res += "|\n";
        }
        return res;
    }

    public static GoMatrix scale(double x, double y, double z) {
        return new GoMatrix(new double[][] {
            { x, 0, 0, 0 },
            { 0, y, 0, 0 },
            { 0, 0, z, 0 },
            { 0, 0, 0, 1 }
        });
    }

    public static GoMatrix unit() {
        return new GoMatrix(new double[][] {
            { 1, 0, 0, 0 },
            { 0, 1, 0, 0 },
            { 0, 0, 1, 0 },
            { 0, 0, 0, 1 }
        });
    }

    public static GoMatrix rotateX(double a) {
        return new GoMatrix(new double[][] {
            { 1,           0,            0, 0 },
            { 0, Math.cos(a), -Math.sin(a), 0 },
            { 0, Math.sin(a),  Math.cos(a), 0 },
            { 0,            0,           0, 1 }
        });
    }

    public static GoMatrix rotateY(double a) {
        return new GoMatrix(new double[][] {
            { Math.cos(a), 0, Math.sin(a), 0 },
            { 0,           1,           0, 0 },
            {-Math.sin(a), 0, Math.cos(a), 0 },
            { 0,           0,           0, 1 }
        });
    }

    public static GoMatrix rotateZ(double a) {
        return new GoMatrix(new double[][] {
            { Math.cos(a), -Math.sin(a), 0, 0 },
            { Math.sin(a),  Math.cos(a), 0, 0 },
            { 0,            0,           1, 0 },
            { 0,            0,           0, 1 }
        });
    }

    public static GoMatrix rotate(GoVector rot) {
        return GoMatrix.rotateZ(rot.com[2])
            .mul(GoMatrix.rotateY(rot.com[1])
            .mul(GoMatrix.rotateX(rot.com[0])));
    }

    public static GoMatrix translate(GoVector d) {
        return new GoMatrix(new double[][] {
            { 1, 0, 0, d.com[0] },
            { 0, 1, 0, d.com[1] },
            { 0, 0, 1, d.com[2] },
            { 0, 0, 0, 1 }
        });
    }
}