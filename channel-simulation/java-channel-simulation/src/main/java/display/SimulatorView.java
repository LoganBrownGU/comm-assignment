package display;

import modulator.Modulator;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimulatorView extends Frame implements Runnable {

    private final int messageLength;
    private final byte[] inputBuffer, outputBuffer;
    public final Object lock = new Object();
    private final Dimension IMAGE_DISPLAY_SIZE = new Dimension(500, 500);
    private final ImageDisplay inputDisplay, outputDisplay;
    private final

    private boolean playing, finished;
    private Modulator modulator;

    private void init() {
        this.setLayout(null);
        this.setSize(1600, 900);
        this.setMenuBar(new MenuBar());



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

    public SimulatorView(Modulator modulator, int messageLength, int pixelsWidth, int pixelsHeight) throws HeadlessException {
        super("Simulation");
        this.modulator = modulator;
        this.messageLength = messageLength;
        this.inputBuffer = new byte[messageLength];
        this.outputBuffer = new byte[messageLength];
        this.inputDisplay = new ImageDisplay(pixelsWidth, pixelsHeight);
        this.outputDisplay = new ImageDisplay(pixelsWidth, pixelsHeight);
    }

    public boolean isFinished() {
        return this.finished;
    }
}
