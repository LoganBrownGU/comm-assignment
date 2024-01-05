package display;

import demodulator.Demodulator;
import modulator.Modulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimulatorView extends Frame implements Runnable {

    public final Object finishedLock = new Object(), imageLock = new Object();
    private static final Dimension IMAGE_DISPLAY_SIZE = new Dimension(700, 700);
    private static final Dimension PADDING = new Dimension(20, 60);
    private static final int minSNR = 1, maxSNR = 240;
    private final ImageDisplay inputDisplay, outputDisplay;
    private final JSlider snrSlider = new JSlider();
    private final TextField snrDisplay = new TextField();
    private final TextField berDisplay = new TextField();
    private final Modulator modulator;
    private final Demodulator demodulator;
    private final int updatePeriod; // milliseconds

    private boolean finished = false, demodulationFinished = false, changeSettings = false;
    private float noiseRMS;

    public static float findBER(byte[] input, byte[] output) {
        if (output.length != input.length) throw new IllegalArgumentException("input data must be same size as output data");

        int errors = 0;
        for (int i = 0; i < input.length; i++) {
            byte in = input[i];
            byte out = output[i];
            for (int j = 0; j < 8; j++) {
                if ((in & 0x01) != (out & 0x01)) errors++;
                in >>= 1; out >>= 1;
            }
        }

        int decPlaces = 3;
        return (float) (Math.round(Math.pow(10, decPlaces) * errors / (input.length * 8)) / Math.pow(10, decPlaces));
    }

    // Initialise all Components, and Frame.
    private void init() {
        this.setLayout(null);
        this.setSize(1600, 900);
        this.setResizable(false);
        Button changeSettingsButton = new Button("Change settings...");
        changeSettingsButton.setLocation(PADDING.width, 30);
        changeSettingsButton.setSize(150, 30);
        changeSettingsButton.addActionListener(e -> {
            synchronized (SimulatorView.this.finishedLock) {
                this.changeSettings = true;
                this.finished = true;
                this.finishedLock.notifyAll();
            }
        });
        this.add(changeSettingsButton);

        this.inputDisplay.setSize(IMAGE_DISPLAY_SIZE);
        this.inputDisplay.setLocation(PADDING.width, PADDING.height);
        this.inputDisplay.setVisible(true);
        this.add(this.inputDisplay);

        this.outputDisplay.setSize(IMAGE_DISPLAY_SIZE);
        this.outputDisplay.setLocation(this.getWidth() - this.outputDisplay.getWidth() - PADDING.width, PADDING.height);
        this.outputDisplay.setVisible(true);
        this.add(this.outputDisplay);

        Dimension sliderLabelSize = new Dimension(50, 20);


        int initialSNR = 24;
        this.snrSlider.setSize(IMAGE_DISPLAY_SIZE.width, 20);
        this.snrSlider.setLocation(this.getWidth() / 2 - this.snrSlider.getWidth() / 2, (int) (this.getHeight() - sliderLabelSize.height * 2.3));
        this.snrSlider.setVisible(true);
        this.snrSlider.setMinimum(minSNR);
        this.snrSlider.setMaximum(maxSNR);
        this.snrSlider.setValue(initialSNR);
        this.snrSlider.addChangeListener(e -> {
            this.snrDisplay.setText(this.snrSlider.getValue() + " dB");
            this.noiseRMS = this.modulator.getRMS() / (float) Math.pow(10, (double) this.snrSlider.getValue() / 20);
        });
        this.add(this.snrSlider);
        this.noiseRMS = this.modulator.getRMS() / (float) Math.pow(10, (double) this.snrSlider.getValue() / 20);

        Label sliderLabel = new Label(minSNR + " dB");
        sliderLabel.setAlignment(Label.RIGHT);
        sliderLabel.setSize(sliderLabelSize);
        sliderLabel.setLocation(this.snrSlider.getX() - sliderLabel.getWidth(), this.snrSlider.getY());
        sliderLabel.setVisible(true);
        this.add(sliderLabel);

        sliderLabel = new Label(maxSNR + " dB");
        sliderLabel.setSize(sliderLabelSize);
        sliderLabel.setLocation(this.snrSlider.getX() + this.snrSlider.getWidth(), this.snrSlider.getY());
        sliderLabel.setVisible(true);
        this.add(sliderLabel);

        sliderLabel = new Label("SNR");
        sliderLabel.setAlignment(Label.CENTER);
        sliderLabel.setSize(sliderLabelSize);
        sliderLabel.setLocation(this.snrSlider.getX() + this.snrSlider.getWidth() / 2 - sliderLabel.getWidth() / 2, this.snrSlider.getY() - sliderLabel.getHeight());
        sliderLabel.setVisible(true);
        this.add(sliderLabel);

        this.snrDisplay.setLocation(this.snrSlider.getX() + this.snrSlider.getWidth() / 2 - sliderLabelSize.width / 2, this.snrSlider.getY() + this.snrSlider.getHeight());
        this.snrDisplay.setSize(sliderLabelSize);
        this.snrDisplay.setVisible(true);
        this.snrDisplay.setEditable(false);
        this.snrDisplay.setText(this.snrSlider.getValue() + " dB");
        this.add(this.snrDisplay);

        this.berDisplay.setSize(sliderLabelSize);
        this.berDisplay.setLocation(this.getWidth() - this.berDisplay.getWidth(), this.outputDisplay.getY() + this.outputDisplay.getHeight());
        this.berDisplay.setEditable(false);
        this.berDisplay.setVisible(true);
        this.add(this.berDisplay);

        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                synchronized (SimulatorView.this.finishedLock) {
                    SimulatorView.this.finished = true;
                    SimulatorView.this.finishedLock.notifyAll();
                }
            }
        });
    }

    @Override
    public void run() {
        init();

        int count = 0;
        while (!this.finished) {
            // When DemodulationController has finished, it will set demodulationFinished to true.
            // Since it is possible that the demodulation buffer will be empty at the start of a new run of the GIF,
            // it is important to check that the demodulation is finished before restarting the demodulation.
            if (this.demodulator.buffer.isEmpty() && this.demodulationFinished) {
                this.demodulator.buffer.clear();
                this.demodulationFinished = false;
                synchronized (this.imageLock) {
                    this.imageLock.notify();
                }
            }

            // Pull one frame's data from the buffer.
            byte[] inputData = this.modulator.buffer.getChunk(this.inputDisplay.getImageWidth() * this.inputDisplay.getImageHeight() * 3);
            this.inputDisplay.paint(inputData);
            // Add the data back into the sender buffer so that the GIF loops.
            this.modulator.buffer.addData(inputData);
            byte[] outputData = this.demodulator.buffer.getChunk(this.outputDisplay.getImageWidth() * this.outputDisplay.getImageHeight() * 3);
            this.outputDisplay.paint(outputData);
            this.berDisplay.setText(Float.toString(findBER(inputData, outputData)));
            if (count++ < 5) System.out.println(findBER(inputData, outputData));

            try {
                Thread.sleep(this.updatePeriod);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void alertFinished() {
        this.demodulationFinished = true;
    }

    public SimulatorView(Modulator modulator, Demodulator demodulator, Dimension imageSize, int framerate) throws HeadlessException {
        super("Simulation");
        this.modulator = modulator;
        this.demodulator = demodulator;
        this.inputDisplay = new ImageDisplay(imageSize.width, imageSize.height);
        this.outputDisplay = new ImageDisplay(imageSize.width, imageSize.height);
        this.updatePeriod = 1000 / framerate;
    }

    public float getNoiseRMS() {
        return this.noiseRMS;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public int getImageSizeBytes() {
        return this.inputDisplay.getImageHeight() * this.inputDisplay.getImageWidth() * 3;
    }

    public boolean getChangeSettings() {
        return this.changeSettings;
    }
}
