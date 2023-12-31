package display;

import demodulator.Demodulator;
import main.SimulationController;
import modulator.Modulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimulatorView extends Frame implements Runnable {

    public final Object lock = new Object();
    private static final Dimension IMAGE_DISPLAY_SIZE = new Dimension(700, 700);
    private static final Dimension PADDING = new Dimension(20, 60);
    private static final int minSNR = 1, maxSNR = 240;
    private final ImageDisplay inputDisplay, outputDisplay;
    private final JSlider snrSlider = new JSlider();
    private final TextField snrDisplay = new TextField();
    private final int updatePeriod; // milliseconds
    private final SimulationController controller;
    private final int framesToPlay;

    private boolean finished = false;
    private Modulator modulator;
    private Demodulator demodulator;

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
        this.outputDisplay.setVisible(true);
        this.add(this.outputDisplay);

        Dimension sliderLabelSize = new Dimension(50, 20);

        this.snrSlider.setSize(IMAGE_DISPLAY_SIZE.width, 20);
        this.snrSlider.setLocation(this.getWidth() / 2 - this.snrSlider.getWidth() / 2, (int) (this.getHeight() - sliderLabelSize.height * 2.3));
        this.snrSlider.setVisible(true);
        this.snrSlider.setMinimum(minSNR);
        this.snrSlider.setMaximum(maxSNR);
        this.snrSlider.addChangeListener(e -> {
            this.snrDisplay.setText(this.snrSlider.getValue() + " dB");
            this.controller.updateSNR(this.snrSlider.getValue(), this.modulator.getRMS());
        });
        this.add(this.snrSlider);

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
        sliderLabel.setLocation(this.snrSlider.getX() + this.snrSlider.getWidth() / 2, this.snrSlider.getY() - sliderLabel.getHeight());
        sliderLabel.setVisible(true);
        this.add(sliderLabel);

        this.snrDisplay.setLocation(this.snrSlider.getX() + this.snrSlider.getWidth() / 2, this.snrSlider.getY() + this.snrSlider.getHeight());
        this.snrDisplay.setSize(sliderLabelSize);
        this.snrDisplay.setVisible(true);
        this.snrDisplay.setEditable(false);
        this.snrDisplay.setText(this.snrSlider.getValue() + " dB");
        this.add(this.snrDisplay);

        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                System.out.println("here");
            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("here1");
            }

            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println("here2");
            }
        });

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
        int frame = 0;

        while (!this.finished) {
            try {
                if (++frame == this.framesToPlay) {
                    frame = 0;
                    this.modulator.buffer.clear();
                    this.demodulator.buffer.clear();
                    this.controller.resume();
                    continue;
                }
                this.inputDisplay.paint();
                this.outputDisplay.paint();
                Thread.sleep(this.updatePeriod);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setModulator(Modulator modulator) {
        this.modulator = modulator;
    }

    public SimulatorView(SimulationController controller, Modulator modulator, Demodulator demodulator, Dimension imageSize, int framerate, int framesToPlay) throws HeadlessException {
        super("Simulation");
        this.controller = controller;
        this.modulator = modulator;
        this.demodulator = demodulator;
        this.inputDisplay = new ImageDisplay(modulator.buffer, imageSize.width, imageSize.height);
        this.outputDisplay = new ImageDisplay(demodulator.buffer, imageSize.width, imageSize.height);
        this.updatePeriod = 1000 / framerate;
        this.framesToPlay = framesToPlay;
    }

    public boolean isFinished() {
        return this.finished;
    }
}
