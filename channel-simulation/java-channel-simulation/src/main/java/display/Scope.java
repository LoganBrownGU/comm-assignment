package display;

import java.awt.*;

public class Scope extends Canvas {

    private Graphics2D graphics = null;
    private double previous = 0, current = 0;
    private double timeStep;

    @Override
    public void paint(Graphics g) {
        if (this.graphics == null) this.graphics = (Graphics2D) g;

        this.graphics.clearRect(0, 0, this.getWidth(), this.getHeight());
        this.graphics.translate(0, -this.getWidth() * timeStep);
        this.graphics.drawLine((int) (this.getWidth() - timeStep), (int) (this.getHeight() * previous), this.getWidth(), (int) (this.getHeight() * current));
    }

    public void update(double sample, double timeStep) {
        this.previous = this.current;
        this.current = sample;
        this.timeStep = timeStep;
    }
}
