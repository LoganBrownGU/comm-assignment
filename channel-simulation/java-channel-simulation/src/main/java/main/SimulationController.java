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
    private byte[] rawData;
    private float noiseRMS = 0;
    private boolean running = true;

    @Override
    public void run() {
        Random rd = new Random();
        Duration sleepTime = Duration.ofNanos((long) (1_000_000_000 * this.timeStep));

        while (!this.interrupted) {
            this.demodulator.reset();

            for (int i = 0; i < this.amp.length && !this.interrupted; i++) {
                this.demodulator.next((float) (rd.nextGaussian() * this.noiseRMS));

            /*try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
            }

            this.running = false;
            synchronized (this.lock) {
                while (!this.running) {
                    try {
                        this.lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
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
        this.interrupted = true;
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
