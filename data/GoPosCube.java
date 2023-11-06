package data;
import java.util.ArrayList;

public class GoPosCube extends GoPosAbstract {
    public GoPosCube(int x, int y, int n, int s) {
        super(x, y, n, s);
    }

    @Override
    public GoPosAbstract changeStone(int s) {
        return new GoPosCube(x, y, n, s);
    }

    public GoPosAbstract shift1(int xs, int ys, int[][] stones) {
        int s = x/n;
        int nx = x + xs; int ny = y + ys;
        int nxr = nx - s*n;

        System.out.println("nxr " + nxr);
        System.out.println("s   " + s);

        if (s == 0) {
            if (nxr < 0)       { nx += 4*n;                     } // l
            else if (ny < 0)   { nx = 6*n-nxr-1; ny = -ny-1;    } // t
            else if (ny >= n)  { nx = 5*n-nxr-1; ny = 2*n-ny-1; } // b
        }
        else if (s == 1) {
            if (ny < 0)        { nx = 5*n-ny-1;  ny = nxr;      } // t
            else if (ny >= 0)  { nx = 3*n+ny;    ny = n-1-nxr;  } // b
        }
        else if (s == 2) {
            if (ny < 0)        { nx += 3*n;      ny += n;       } // t
            else if (ny >= n)  { nx += 2*n;      ny -= n;       } // b
        }
        else if (s == 3) {
            if (ny < 0)        { nx = 6*n+ny;    ny = n-nxr-1;  } // t
            else if (ny >= n)  { nx = 6*n-ny-1;   ny = nxr;     } // b
            else if (nxr >= n) { nx -= 4*n;                     } // r
        }
        else if (s == 4) {
            if (ny < 0)       { nx -= 2*n;       ny += n;       } // t
            else if (ny >= n) { nx = n-1-nxr;    ny = 2*n-ny-1; } // b
            else if (nxr < 0) { nx = 2*n-ny-1;   ny = n+nxr;    } // l
            else if (nxr >= n){ nx = 3*n+ny;     ny = 2*n-nxr-1;} // r
        }
        else if (s == 5) {
            if (ny < 0)       { nx = n-1-nxr;    ny = -ny-1;    } // t
            else if (ny >= n) { nx -= 3*n;       ny -= n;       } // b
            else if (nxr < 0) { nx = 1*n+ny;     ny = -nxr-1;   } // l
            else if (nxr >= n){ nx = 4*n-1-ny;   ny = nxr-n;    } // r
        }

        return new GoPosCube(nx, ny, stones.length, stones[y][x]);
    } 

    @Override
    public ArrayList<GoPosAbstract> neighbours(int[][] stones) {
        ArrayList<GoPosAbstract> result = new ArrayList<>();
        result.add(shift1(1, 0, stones));
        result.add(shift1(0, 1, stones));
        result.add(shift1(-1, 0, stones));
        result.add(shift1(0, -1, stones));
        return result;
    }
}