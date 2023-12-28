package display;

import modulator.Modulator;

import java.awt.*;

public class SimulatorView extends Frame implements Runnable {

    private Modulator modulator;
    private final int MESSAGE_LENGTH;
    private final byte[] inputBuffer, outputBuffer;

    private void init() {

    }

    @Override
    public void run() {

    }

    public void setModulator(Modulator modulator) {
        this.modulator = modulator;
    }

    public SimulatorView(String title, Modulator modulator, int MESSAGE_LENGTH) throws HeadlessException {
        super(title);
        this.modulator = modulator;
        this.MESSAGE_LENGTH = MESSAGE_LENGTH;
        this.inputBuffer = new byte[MESSAGE_LENGTH];
        this.outputBuffer = new byte[MESSAGE_LENGTH];
    }
}
