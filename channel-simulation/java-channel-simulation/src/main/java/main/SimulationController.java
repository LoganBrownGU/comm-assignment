package main;

import demodulator.Demodulator;

import java.time.Duration;
import java.util.Random;

public class SimulationController implements Runnable {
    private final float timeStep;
    private final float[] amp;
    private boolean interrupted = false;
    private final Demodulator demodulator;
    private float noiseRMS = 0;

    @Override
    public void run() {
        Random rd = new Random();
        Duration sleepTime = Duration.ofNanos((long) (1_000_000_000 * this.timeStep));

        for (int i = 0; i < this.amp.length && !this.interrupted; i++) {
            this.demodulator.next((float) (rd.nextGaussian() * this.noiseRMS));

            /*try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
        }
    }

    public SimulationController(float timeStep, float[] amp, Demodulator demodulator) {
        this.timeStep = timeStep;
        this.amp = amp;
        this.demodulator = demodulator;
    }

    public void interrupt() {
        this.interrupted = true;
    }

    public void updateSNR(int snr, float modulatorRMS) {
        this.noiseRMS = modulatorRMS / (float) Math.pow(10, (double) snr / 20);
    }
}
