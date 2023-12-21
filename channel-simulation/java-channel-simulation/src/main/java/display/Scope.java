package display;

import java.awt.*;
import java.util.ArrayList;

public class Scope extends Canvas {

    private Graphics2D graphics = null;
    private final ArrayList<Double> amplitudes;
    private final ArrayList<Double> backlog = new ArrayList<>();
    private final double xScale, yScale, start, step;

    @Override
    public void paint(Graphics g) {
        if (this.graphics == null) this.graphics = (Graphics2D) this.getGraphics();

        this.graphics.clearRect(0, 0, this.getWidth(), this.getHeight());
        this.graphics.setColor(Color.WHITE);

        int prevX = 0, prevY = (int) (this.backlog.get(0) * this.getHeight() / 2) + this.getHeight() / 2;
        for (double d : this.backlog.subList(1, this.backlog.size() - 1)) {
            int y = (int) (d * this.getHeight() / 2) + this.getHeight() / 2;
            int x = prevX + Math.max(this.getWidth() / this.backlog.size(), 1);

            this.graphics.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }
    }

    public void update(double time) {

        int start = (int) Math.max(0, (time - this.start - this.xScale) / this.step);
        int end = (int) Math.max(0, (time - this.start) / this.step);

        this.backlog.clear();
        this.backlog.addAll(this.amplitudes.subList(start, end+1));

        paint(this.graphics);
    }

    // Scales refer to how much should be shown. E.g. if xScale = 0.1, 0.1 seconds of data would be displayed at any
    // given time.
    public Scope(ArrayList<Double> amplitudes, double start, double step, double xScale, double yScale) {
        this.amplitudes = amplitudes;
        this.xScale = xScale;
        this.yScale = yScale;
        this.start = start;
        this.step = step;
    }
}
