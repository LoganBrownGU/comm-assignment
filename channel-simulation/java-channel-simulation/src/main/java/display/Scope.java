package display;

import java.awt.*;
import java.util.ArrayList;

public class Scope extends Canvas {

    private Graphics2D graphics = null;
    private double timeStep;
    private final ArrayList<Double> backlog = new ArrayList<>();
    private int backlogSize = 1000;

    @Override
    public void paint(Graphics g) {
        if (this.graphics == null) this.graphics = (Graphics2D) this.getGraphics();

        this.graphics.clearRect(0, 0, this.getWidth(), this.getHeight());
        this.graphics.setColor(Color.WHITE);

        int prevX = 0, prevY = 0;
        for (double d : this.backlog) {
            int y = (int) (d * this.getHeight() / 2) + this.getHeight() / 2;
            int x = prevX + Math.max(this.getWidth() / this.backlogSize, 1);

            this.graphics.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }
    }

    public void update(double sample, double timeStep) {
        this.backlog.add(sample);
        this.backlogSize = (int) (1 / timeStep);
        if (this.backlog.size() > this.backlogSize) this.backlog.remove(0);
        this.timeStep = timeStep;
        this.paint(this.graphics);
    }
}
