package math;
import java.awt.Color;
public class GoColor {
    public final double[] rgba;
    public GoColor(double[] rgba) { this.rgba = rgba; }
    public GoColor(double r, double g, double b, double a) {
        rgba = new double[]{ r,g,b,a };
    }
    public GoColor mix(GoColor that, double k) {
        double[] new_rgba = new double[rgba.length];
        for (int i = 0; i < rgba.length; i ++) {
            new_rgba[i] = (that.rgba[i] - this.rgba[i])*k + this.rgba[i];
        }
        return new GoColor(new_rgba);
    }
    public Color toAWT() { return new Color(
        (float) rgba[0],
        (float) rgba[1],
        (float) rgba[2],
        (float) rgba[3]
    ); }
    public static GoColor convertAWT(Color color) {
        return new GoColor(
            (double)color.getRed()/255.0,  
            (double)color.getGreen()/255.0,
            (double)color.getBlue()/255.0,
            (double)color.getAlpha()/255.0
        );
    }
}
