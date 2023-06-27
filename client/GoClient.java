package client;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import math.*;
import data.*;

public class GoClient {
    public static void main(String[] args) {
        GoState state = new GoState(8, "2345", true);
        System.out.println("State____\n" + state);
        GoViewer viewer = new GoViewer(state);
        viewer.init();
    }
}