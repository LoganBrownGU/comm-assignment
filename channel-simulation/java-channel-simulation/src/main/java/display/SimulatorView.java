package display;

import demodulator.Demodulator;
import modulator.Modulator;
import util.Buffer;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimulatorView extends Frame implements Runnable {

    public final Object lock = new Object();
    private static final Dimension IMAGE_DISPLAY_SIZE = new Dimension(500, 500);
    private static final Dimension PADDING = new Dimension(20, 60);
    private final ImageDisplay inputDisplay, outputDisplay;
    private final int updatePeriod; // milliseconds

    private boolean playing = true, finished;
    private Modulator modulator;

    private void init() {
        this.setLayout(null);
        this.setSize(1600, 900);
        this.setResizable(false);
        this.setMenuBar(new MenuBar());

        this.inputDisplay.setSize(IMAGE_DISPLAY_SIZE);
        this.inputDisplay.setLocation(PADDING.width, PADDING.height);
        this.inputDisplay.setVisible(true);
        this.add(this.inputDisplay);

        this.outputDisplay.setSize(IMAGE_DISPLAY_SIZE);
        this.outputDisplay.setLocation(this.getWidth() - this.outputDisplay.getWidth() - PADDING.width, PADDING.height);
        this.setVisible(true);
        this.add(this.outputDisplay);

        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                synchronized (SimulatorView.this.lock) {
                    SimulatorView.this.finished = true;
                    SimulatorView.this.lock.notifyAll();
                }
            }
        });
    }

    @Override
    public void run() {
        init();

        while (!this.finished && this.playing) {
            try {
                this.inputDisplay.paint();
                Thread.sleep(this.updatePeriod);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setModulator(Modulator modulator) {
        this.modulator = modulator;
    }

    public SimulatorView(Modulator modulator, Buffer outputBuffer, Dimension imageSize, int framerate) throws HeadlessException {
        super("Simulation");
        this.modulator = modulator;
        this.inputDisplay = new ImageDisplay(modulator.buffer, imageSize.width, imageSize.height);
        this.outputDisplay = new ImageDisplay(outputBuffer, imageSize.width, imageSize.height);
        this.updatePeriod = 1000 / framerate;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isPlaying() {
        return this.playing;
    }
}
