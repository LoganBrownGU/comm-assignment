package display;

import modulator.Modulator;
import util.Buffer;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimulatorView extends Frame implements Runnable {

    private final int messageLength;
    public final Object lock = new Object();
    private static final Dimension IMAGE_DISPLAY_SIZE = new Dimension(500, 500);
    private static final Dimension PADDING = new Dimension(20, 60);
    private final ImageDisplay inputDisplay, outputDisplay;

    private boolean playing, finished;
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
    }

    public void setModulator(Modulator modulator) {
        this.modulator = modulator;
    }

    public SimulatorView(Modulator modulator, Buffer outputBuffer, int messageLength, int pixelsWidth, int pixelsHeight) throws HeadlessException {
        super("Simulation");
        this.modulator = modulator;
        this.messageLength = messageLength;
        this.inputDisplay = new ImageDisplay(modulator.buffer, pixelsWidth, pixelsHeight);
        this.outputDisplay = new ImageDisplay(outputBuffer, pixelsWidth, pixelsHeight);
    }

    public boolean isFinished() {
        return this.finished;
    }
}
