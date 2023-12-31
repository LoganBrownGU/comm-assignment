package main;

import demodulator.Demodulator;
import display.SimulatorView;

import java.util.Random;

public class DemodulationController implements Runnable {

    public final Object lock = new Object();
    private final float timeStep;
    private final float[] amp;
    private final SimulatorView simulatorView;
    private final Demodulator demodulator;

    @Override
    public void run() {
        Random rd = new Random();
        long waitTime = (long) (1_000_000_000f * this.timeStep);

        while (true) {
            this.demodulator.reset();

            for (int i = 0; i < this.amp.length; i++) {
                long calcTime = System.nanoTime();

                this.demodulator.next((float) (rd.nextGaussian() * this.simulatorView.getNoiseRMS()));

                calcTime = System.nanoTime() - calcTime;
                long deltaTime = waitTime - calcTime;
                if (deltaTime <= 0) continue;

                long millis = deltaTime / 1_000_000;
                int nanos = millis == 0 ? (int) deltaTime : (int) (deltaTime % millis);
                try {
                    Thread.sleep(millis, nanos);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            // Sometimes the demodulator won't output the final byte, so need to make sure that there's a multiple
            // of 3 bytes in the buffer.
            for (int i = 0; i < this.demodulator.buffer.getSize() % 3; i++) this.demodulator.buffer.addData((byte) 0x00);

            this.simulatorView.alertFinished();
            synchronized (this.simulatorView.imageLock) {
                try {
                    this.simulatorView.imageLock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public DemodulationController(Demodulator demodulator, SimulatorView simulatorView, int framerate, int nFrames, float[] amp) {
        this.timeStep = (float) nFrames / (framerate * amp.length);
        this.amp = amp;
        this.simulatorView = simulatorView;
        this.demodulator = demodulator;
    }
}
