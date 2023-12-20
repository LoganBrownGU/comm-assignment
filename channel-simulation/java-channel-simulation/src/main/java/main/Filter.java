package main;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;

public class Filter {
    private final int fLow, fHigh;

    private double[] filter(double[] fft) {
        double[] output = new double[fft.length];
        for (int i = 0; i < this.fLow; i++) {
            output[i * 2] = 0;
            output[i * 2 + 1] = 0;
        }

        for (int i = this.fLow; i < this.fHigh; i++) {
            output[i*2] = fft[i*2];
            output[i*2+1] = fft[i*2+1];
        }

        for (int i = this.fHigh; i < fft.length / 2; i++) {
            output[i * 2] = 0;
            output[i * 2 + 1] = 0;
        }

        return output;
    }

    public ArrayList<Double> calculate(ArrayList<Double> samples) {
        // turn into array
        double[] fft = new double[samples.size() * 2];
        for (int i = 0; i < samples.size(); i++) fft[i] = samples.get(i);

        DoubleFFT_1D fft1D = new DoubleFFT_1D(samples.size());
        fft1D.realForward(fft);
        fft = filter(fft);
        fft1D.complexInverse(fft, false);

        ArrayList<Double> output = new ArrayList<>();
        for (int i = 0; i < fft.length / 2; i++)
            output.add(fft[i*2] / (samples.size() / 2));

        return output;
    }

    public Filter(int fLow, int fHigh) {
        this.fLow = fLow;
        this.fHigh = fHigh;
    }
}
