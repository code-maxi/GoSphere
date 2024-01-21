package client;
import java.awt.*;
import data.*;

public class GoCanvasCube extends GoCanvasAbstract {
    public GoCanvasCube(GoViewer viewer) { super(viewer); }

    @Override
    public synchronized void setState(GoStateAbstract state) {
        this.state = state;
        this.hover_pos = null;

        for (int y = 0; y < state.stones.length; y ++) {
            for (int x = 0; x < state.n*6; x ++) {
                GoPosAbstract pos = state.posOnMe(x, y);
                GoCanvasStoneAbstract stone = stones.get(pos.toString());
                
                if (stone == null) putStone(GoCanvasCubeFace.createFace(pos, state.colors[y][x]));
                else stone.setPosColor(pos, GoStateAbstract.color(state.colors[y][x]));
            }
        }

        repaint();
    }

    @Override
    public void paintMe(Graphics2D g2) {
        for (GoCanvasStoneAbstract s : stones.values()) { s.paint(g2, 1, hover_pos_paint); }
        for (GoCanvasStoneAbstract s : stones.values()) { s.paint(g2, 0, hover_pos_paint); }

        /*double size = 10;
        g2.setColor(Color.ORANGE);
        GoVector center = Trm.apply(new GoVector());
        g2.fill(new Ellipse2D.Double(center.com[0], center.com[1], size, size));*/
    }
}