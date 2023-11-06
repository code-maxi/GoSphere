package client;
import java.awt.*;
import java.awt.geom.Ellipse2D;

import data.*;
import math.GoVector;

import java.util.ArrayList;
import java.util.Collections;

public class GoCanvasCube extends GoCanvasAbstract {
    private ArrayList<GoCanvasCubeFace> sortedStones = new ArrayList<>();
    public GoCanvasCube(GoViewer viewer) { super(viewer); }

    @Override
    public synchronized void setState(GoStateAbstract state) {
        this.state = state;
        this.hover_pos = null;
        sortedStones.clear();
        stones.clear();

        
        for (int y = 0; y < state.stones.length; y ++) {
            for (int x = 0; x < state.n*6; x ++) {
                GoPosCube pos = new GoPosCube(x, y, state.n, state.stones[y][x]);
                putStone(GoCanvasCubeFace.createFace(pos));
            }
        }

        System.out.println("Updating... " + stones.size());

        // add faces

        for (GoCanvasStoneAbstract stone : stones.values())
            sortedStones.add((GoCanvasCubeFace) stone);

        repaint();
    }

    @Override
    protected synchronized void updateMe() {
        super.updateMe();
        Collections.sort(sortedStones);
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

        for (GoCanvasStoneAbstract s : sortedStones) {
            GoCanvasCubeFace cubeFace = (GoCanvasCubeFace) s;
            cubeFace.paint(g2, -1, hover_pos);
        }

        double size = 10;
        g2.setColor(Color.ORANGE);
        GoVector center = Trm.apply(new GoVector());
        g2.fill(new Ellipse2D.Double(center.com[0], center.com[1], size, size));
    }
}