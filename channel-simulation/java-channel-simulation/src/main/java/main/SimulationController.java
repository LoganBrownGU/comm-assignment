package main;

import demodulator.Demodulator;
import modulator.Modulator;

import java.time.Duration;
import java.util.Random;

public class SimulationController implements Runnable {

    public final Object lock = new Object();
    private final float timeStep;
    private final float[] amp;
    private boolean interrupted = false;
    private final Modulator modulator;
    private final Demodulator demodulator;
    private final byte[] rawData;
    private float noiseRMS = 0;
    private boolean running = true;

    @Override
    public void run() {
        Random rd = new Random();

        while (!this.interrupted) {
            this.demodulator.reset();

            for (int i = 0; i < this.amp.length && !this.interrupted; i++) {
                this.demodulator.next((float) (rd.nextGaussian() * this.noiseRMS));
            }
            if (this.interrupted) break;

            this.running = false;
            synchronized (this.lock) {
                while (!this.running && !this.interrupted) {
                    try {
                        this.lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (this.interrupted) break;

            this.modulator.buffer.addData(this.rawData);
        }
    }

    public SimulationController(float timeStep, float[] amp, byte[] data, Modulator modulator, Demodulator demodulator) {
        this.timeStep = timeStep;
        this.amp = amp;
        this.modulator = modulator;
        this.demodulator = demodulator;
        this.rawData = data;
    }

    public void interrupt() {
        synchronized (this.lock) {
            this.interrupted = true;
            this.lock.notifyAll();
        }
    }

    public void updateSNR(int snr, float modulatorRMS) {
        this.noiseRMS = modulatorRMS / (float) Math.pow(10, (double) snr / 20);
    }

    public void resume() {
        synchronized (this.lock) {
            this.running = true;
            this.lock.notify();
        }
    }
}
