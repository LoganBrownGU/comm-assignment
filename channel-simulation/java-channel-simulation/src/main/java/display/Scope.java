package display;

import java.awt.*;
import java.util.ArrayList;

public class Scope extends Canvas {

    private Graphics2D graphics = null;
    private double previous = 0, current = 0;
    private double timeStep;
    private final ArrayList<Double> backlog = new ArrayList<>();

    @Override
    public void paint(Graphics g) {
        if (this.graphics == null) this.graphics = (Graphics2D) this.getGraphics();

        this.graphics.clearRect(0, 0, this.getWidth(), this.getHeight());
        graphics.setColor(Color.WHITE);
        this.graphics.translate(this.getWidth() * timeStep, 0);
        int yVal = this.getHeight() / 2 + (int) (this.getHeight() * previous / 2);
        this.graphics.drawLine((int) (this.getWidth() - timeStep), yVal, this.getWidth(), (int) (this.getHeight() * current));
    }

    public void update(double sample, double timeStep) {
        this.backlog.add(sample);
        if (this.backlog.size() > this.getWidth()) this.backlog.remove(0);
        this.timeStep = timeStep;
        this.paint(this.graphics);
    }
}
