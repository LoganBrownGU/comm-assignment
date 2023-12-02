package display;

import java.awt.*;
import java.util.ArrayList;

public class Scope extends Canvas {

    private Graphics2D graphics = null;
    private final ArrayList<Double> backlog = new ArrayList<>();
    private int backlogSize = 1000;
    private int frame = 0;
    private final double FRAME_PERIOD = 1d / 60d;
    private final double X_SCALE = 2;
    private double time = 0;

    @Override
    public void paint(Graphics g) {
        if (this.graphics == null) this.graphics = (Graphics2D) this.getGraphics();

        this.graphics.clearRect(0, 0, this.getWidth(), this.getHeight());
        this.graphics.setColor(Color.WHITE);

        int prevX = 0, prevY = (int) (this.backlog.get(0) * this.getHeight() / 2) + this.getHeight() / 2;
        for (double d : this.backlog.subList(1, this.backlog.size() - 1)) {
            int y = (int) (d * this.getHeight() / 2) + this.getHeight() / 2;
            int x = prevX + Math.max(this.getWidth() / this.backlogSize, 1);

            this.graphics.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }
    }

    public void update(double sample, double timeStep) {
        this.backlog.add(sample);
        this.backlogSize = (int) (this.X_SCALE / timeStep);
        this.time += timeStep;
        if (this.backlog.size() > this.backlogSize) this.backlog.remove(0);

        if (Math.floor(this.time / this.FRAME_PERIOD) == this.frame) return;
        this.frame++;
        if (this.backlog.size() > 1) this.paint(this.graphics);
    }
}
