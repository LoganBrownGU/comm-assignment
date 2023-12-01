package main;

import org.jfree.data.xy.XYDataItem;
import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;

public class Filter {
    private final int fLow, fHigh;
    // number of samples to store
    private final int BACKLOG_SIZE = 1000;
    private final DoubleFFT_1D fft1D = new DoubleFFT_1D(BACKLOG_SIZE);
    private final ArrayList<Double> samples = new ArrayList<>();
    private double[] outputs = new double[BACKLOG_SIZE * 2];
    private int fftIdx = 0;

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

    public double output(double sample) {
        samples.add(sample);
        if (fftIdx == BACKLOG_SIZE) {
            // fft of BACKLOG_SIZE oldest samples
            double[] fft = new double[BACKLOG_SIZE * 2];
            for (int i = 0; i < BACKLOG_SIZE; i++) fft[i] = samples.remove(0);
            fft1D.realForward(fft);
            fft = filter(fft);
            fft1D.complexInverse(fft, false);
            outputs = fft;
            fftIdx = 0;
        }

        return outputs[fftIdx++ * 2] / ((double) BACKLOG_SIZE / 2);
    }

    public Filter(int fLow, int fHigh) {
        this.fLow = fLow;
        this.fHigh = fHigh;
    }
}
