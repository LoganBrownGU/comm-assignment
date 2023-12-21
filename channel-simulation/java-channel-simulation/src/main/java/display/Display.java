package display;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class Display extends Frame implements Runnable {

    private final Scope transmitterScope, channelScope;
    private final TextField dataIn, dataOut;
    private final Button startButton, pauseButton, resetButton;
    private boolean finished = false;
    private boolean playing = false;
    private static final int REFRESH_RATE = 60;

    private final double start, end, step;
    private double time;

    private String byteToString(byte b) {
        String intStr = Integer.toBinaryString(b);
        StringBuilder outStr = new StringBuilder();
        if (intStr.length() > 8) {
            outStr = new StringBuilder(intStr.substring(24));
        } else {
            outStr.append("0".repeat(8 - intStr.length()));
            outStr.append(intStr);
        }

        return outStr.toString();
    }

    private void update() {
        this.transmitterScope.update(this.time);
        this.channelScope.update(this.time);
    }

    private void init() {
        this.setSize(1600, 900);
        Color scopeColour = new Color(0x000088);

        this.transmitterScope.setSize(this.getWidth() / 2 - 10, this.getHeight() / 2);
        this.transmitterScope.setLocation(0, 10);
        this.transmitterScope.setBackground(scopeColour);
        this.transmitterScope.setVisible(true);
        this.add(this.transmitterScope);

        this.channelScope.setSize(this.getWidth() / 2 - 10, this.getHeight() / 2);
        this.channelScope.setLocation(this.getWidth() / 2 + 10, 10);
        this.channelScope.setBackground(scopeColour);
        this.channelScope.setVisible(true);
        this.add(this.channelScope);

        this.dataIn.setSize(100, 20);
        this.dataIn.setLocation(0, this.transmitterScope.getY() + this.transmitterScope.getHeight());
        this.dataIn.setVisible(true);
        this.add(this.dataIn);

        this.dataOut.setSize(100, 20);
        this.dataOut.setLocation(this.getWidth() - this.dataOut.getWidth(), this.channelScope.getY() + this.channelScope.getHeight());
        this.dataOut.setVisible(true);
        this.add(this.dataOut);

        this.startButton.setSize(20, 20);
        this.startButton.setLocation(this.getWidth() / 2 - this.startButton.getWidth() / 2, this.getHeight() - this.startButton.getHeight());
        this.startButton.addActionListener(e -> this.playing = true);
        this.startButton.setVisible(true);
        this.add(this.startButton);

        this.pauseButton.setSize(20, 20);
        this.pauseButton.setLocation(this.getWidth() / 2 + this.startButton.getWidth() / 2, this.getHeight() - this.startButton.getHeight());
        this.pauseButton.addActionListener(e -> this.playing = false);
        this.pauseButton.setVisible(true);
        this.add(this.pauseButton);

        this.resetButton.setSize(20, 20);
        this.resetButton.setLocation(this.getWidth() / 2 - this.startButton.getWidth() / 2 - this.resetButton.getWidth(), this.getHeight() - this.startButton.getHeight());
        this.resetButton.addActionListener(e -> {
            this.playing = false;
            this.time = this.start;
        });
        this.resetButton.setVisible(true);
        this.add(this.resetButton);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Display.this.finished = true;
                synchronized (Display.this) {
                    Display.this.notifyAll();
                }
            }
        });

        this.setLayout(null);
        this.setVisible(true);
    }

    @Override
    public void run() {
        init();

        while (!this.finished) {
            try {
                Thread.sleep(1000 / REFRESH_RATE);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (!this.playing) continue;

            this.time += 1d / REFRESH_RATE;
            this.dataIn.setText(Double.toString(this.time));
            update();
        }
    }

    public Display(String title, double start, double end, double step, ArrayList<Double> transmitterData, ArrayList<Double> channelData) throws HeadlessException {
        super(title);
        this.transmitterScope = new Scope(transmitterData, start, step, .1, 2);
        this.channelScope = new Scope(channelData, start, step, .1, 2);
        this.dataIn = new TextField();
        this.dataOut = new TextField();
        this.startButton = new Button("\u23F5");
        this.pauseButton = new Button("\u23F8");
        this.resetButton = new Button("\u23EE");
        this.step = step;
        this.start = start;
        this.end = end;
        this.time = start;
    }

    public boolean isFinished() {
        return this.finished;
    }
}
