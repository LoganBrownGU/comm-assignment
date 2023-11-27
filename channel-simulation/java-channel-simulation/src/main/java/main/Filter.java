package main;

import org.jfree.data.xy.XYDataItem;
import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;

public class Filter {
    private int fLow, fHigh;
    // number of samples to store
    private final int BACKLOG_SIZE = 1000;
    private final ArrayList<Double> samples = new ArrayList<>();

    private void filter(double[] fft) {
        for (int i = 0; i < Math.min(fLow, fft.length / 2); i++) {
            fft[i * 2] = 0;
            fft[i * 2 + 1] = 0;
        }

        for (int i = fHigh; i < fft.length / 2; i++) {
            fft[i * 2] = 0;
            fft[i * 2 + 1] = 0;
        }
    }

    public double output(double sample) {
        samples.add(0, sample);

        if (samples.size() > BACKLOG_SIZE) samples.remove(samples.size() - 1);

        if (samples.size() != BACKLOG_SIZE) return sample;

        double[] fft = new double[samples.size() * 2];
        for (int i = 0; i < samples.size(); i++) fft[i] = samples.get(i);
        DoubleFFT_1D transform = new DoubleFFT_1D(samples.size());
        transform.realForward(fft);
        filter(fft);
        transform.complexInverse(fft, false);


        return fft[fft.length - 2] / (double) (samples.size() / 2);
    }

    public Filter(int fLow, int fHigh) {
        this.fLow = fLow;
        this.fHigh = fHigh;
    }
}
