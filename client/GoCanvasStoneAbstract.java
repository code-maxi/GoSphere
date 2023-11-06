package client;

import java.awt.Graphics2D;

import data.GoPosAbstract;
import math.GoColor;
import math.GoMatrix;

public interface GoCanvasStoneAbstract {
    public void update(GoMatrix Trm);
    public void paint(Graphics2D g2, int layer, GoPosAbstract hover);
    public boolean isHovered(double x, double y);
    public GoPosAbstract getPos();
    public void setPos(GoPosAbstract pos);
    public void setColor(GoColor color);
}
