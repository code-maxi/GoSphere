package client;
import java.awt.*;
import data.*;

public class GoCanvasCube extends GoCanvasAbstract {
    public GoCanvasCube(GoViewer viewer) { super(viewer); }

    @Override
    public synchronized void setState(GoStateAbstract state) {
        this.state = state;
        this.hover_pos = null;
        stones.clear();

        
        for (int y = 0; y < state.stones.length; y ++) {
            for (int x = 0; x < state.n*6; x ++) {
                GoPosCube pos = new GoPosCube(x, y, state.n, state.stones[y][x]);
                putStone(GoCanvasCubeFace.createFace(pos, state.colors[y][x]));
            }
        }

        repaint();
    }

    @Override
    public void paintMe(Graphics2D g2) {
        /*g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(
            RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);*/

        for (GoCanvasStoneAbstract s : stones.values()) { s.paint(g2, 1, hover_pos); }
        for (GoCanvasStoneAbstract s : stones.values()) { s.paint(g2, 0, hover_pos); }

        /*double size = 10;
        g2.setColor(Color.ORANGE);
        GoVector center = Trm.apply(new GoVector());
        g2.fill(new Ellipse2D.Double(center.com[0], center.com[1], size, size));*/
    }
}