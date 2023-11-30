package main;

import org.jfree.data.xy.XYDataItem;
import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;

public class Filter {
    private int fLow, fHigh;
    // number of samples to store
    private final int BACKLOG_SIZE = 1000;
    private final DoubleFFT_1D fft1D = new DoubleFFT_1D(BACKLOG_SIZE);
    private final ArrayList<Double> samples = new ArrayList<>();
    private final double[] fft = new double[2 * BACKLOG_SIZE];
    int fftIdx = 0;
    private boolean filtering = false;

    private double[] filter(double[] fft) {
        double[] output = new double[fft.length];
        for (int i = 0; i < fLow; i++) {
            output[i * 2] = 0;
            output[i * 2 + 1] = 0;
        }

        for (int i = fLow; i < fHigh; i++) {
            output[i*2] = fft[i*2];
            output[i*2+1] = fft[i*2+1];
        }

        for (int i = fHigh; i < fft.length / 2; i++) {
            output[i * 2] = 0;
            output[i * 2 + 1] = 0;
        }

        return output;
    }

    private void shiftDown(double[] arr) {
        for (int i = 1; i < arr.length; i++)
            arr[i-1] = arr[i];
    }

    public double output(double sample) {
        this.samples.add(sample);

        if (this.fftIdx == BACKLOG_SIZE && !this.filtering) {
            this.filtering = true;
            fft1D.realForward(fft);
        }

        if (!this.filtering) {
            this.fft[this.fftIdx++] = sample;
            return sample;
        }

        double oldSample = samples.remove(0);
        for (int k = 0; k < BACKLOG_SIZE; k++) {
            this.fft[k*2] = (this.fft[k*2] - sample + samples.get(samples.size() - 1)) * Math.cos(2 * Math.PI * k / BACKLOG_SIZE);
            this.fft[k*2+1] = (this.fft[k*2+1] - sample + samples.get(samples.size() - 1)) * Math.sin(2 * Math.PI * k / BACKLOG_SIZE);
        }
        /*for (int k = 0; k < BACKLOG_SIZE; k++) {
            this.fft[k*2] += sample * Math.cos((-2 * Math.PI * k * (BACKLOG_SIZE - 1)) / BACKLOG_SIZE) - oldSample;
            this.fft[k*2+1] += sample * Math.sin((-2 * Math.PI * k * (BACKLOG_SIZE - 1)) / BACKLOG_SIZE) - oldSample;
        }*/
        /*shiftDown(fft);
        for (int n = 0; n < samples.size(); n++) {
            this.fft[fft.length - 2] += sample * Math.cos((-2 * Math.PI * n * (BACKLOG_SIZE - 1)) / BACKLOG_SIZE);
            this.fft[fft.length - 1] += sample * Math.sin((-2 * Math.PI * n * (BACKLOG_SIZE - 1)) / BACKLOG_SIZE);
        }*/

        for (int i = 0; i < BACKLOG_SIZE; i++) {
            this.fft[i] = samples.get(i);
        }
        fft1D.realForward(fft);

        double[] filtered = new double[this.fft.length];
        System.arraycopy(this.fft, 0, filtered, 0, this.fft.length); //filter(this.fft);
        this.fft1D.complexInverse(filtered, false);

        assert samples.size() == BACKLOG_SIZE;
        return filtered[filtered.length - 2] / (double) (BACKLOG_SIZE / 2);
    }

    public Filter(int fLow, int fHigh) {
        this.fLow = fLow;
        this.fHigh = fHigh;
    }
}
