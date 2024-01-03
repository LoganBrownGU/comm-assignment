package main;

import demodulator.Demodulator;
import display.SimulatorView;

import java.util.Random;

public class DemodulationController implements Runnable {

    private final float[] samples;
    private final SimulatorView simulatorView;
    private final Demodulator demodulator;

    @Override
    public void run() {
        Random rd = new Random();
        boolean interrupted = false;

        while (!interrupted) {
            this.demodulator.reset();

            for (int i = 0; i < this.samples.length; i++)
                this.demodulator.next((float) (rd.nextGaussian() * this.simulatorView.getNoiseRMS()));


            synchronized (this.demodulator.buffer) {
                // Sometimes the demodulator won't output the last bytes, or will output too many.

                while (this.demodulator.buffer.getSize() % this.simulatorView.getImageSizeBytes() != 0)
                    this.demodulator.buffer.addData((byte) 0x00);
                this.demodulator.buffer.notifyAll();
            }

            // Tell the simulator window that the demodulation has finished.
            this.simulatorView.alertFinished();
            // Wait for the simulator to finish the frame.
            synchronized (this.simulatorView.imageLock) {
                try {
                    this.simulatorView.imageLock.wait();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        }
    }

    public DemodulationController(Demodulator demodulator, SimulatorView simulatorView, float[] samples) {
        this.samples = samples;
        this.simulatorView = simulatorView;
        this.demodulator = demodulator;
    }
}
