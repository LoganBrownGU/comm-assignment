package main;

import demodulator.Demodulator;
import display.SimulatorView;

import java.util.Random;

public class DemodulationController implements Runnable {

    private final float[] amp;
    private final SimulatorView simulatorView;
    private final Demodulator demodulator;

    @Override
    public void run() {
        Random rd = new Random();
        boolean interrupted = false;

        while (!interrupted) {
            this.demodulator.reset();

            for (int i = 0; i < this.amp.length; i++)
                this.demodulator.next((float) (rd.nextGaussian() * this.simulatorView.getNoiseRMS()));


            // Sometimes the demodulator won't output the final byte, so need to make sure that there's a multiple
            // of 3 bytes in the buffer.
            for (int i = 0; i < this.demodulator.buffer.getSize() % 3; i++) this.demodulator.buffer.addData((byte) 0x00);

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

    public DemodulationController(Demodulator demodulator, SimulatorView simulatorView, float[] amp) {
        this.amp = amp;
        this.simulatorView = simulatorView;
        this.demodulator = demodulator;
    }
}
