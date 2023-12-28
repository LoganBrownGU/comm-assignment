package display;

import java.awt.*;

public class Oscilloscope extends Canvas {

    private double xSpan, ySpan;
    private final Graphics2D g;

    public void paint() {

    }

    public Oscilloscope(double xSpan, double ySpan) {
        this.xSpan = xSpan;
        this.ySpan = ySpan;
        this.g = (Graphics2D) super.getGraphics();
    }

    public void setxSpan(double xSpan) {
        this.xSpan = xSpan;
    }

    public void setySpan(double ySpan) {
        this.ySpan = ySpan;
    }
}
