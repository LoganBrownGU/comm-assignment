package main;

import org.jfree.data.xy.XYDataItem;
import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;

public class Filter {
    private int fLow, fHigh;
    // number of samples to store
    private final int BACKLOG_SIZE = 1000;
    private final ArrayList<Double> samples = new ArrayList<>();
    private boolean graphed = false;
    private int count = 0;

    private void filter(double[] fft) {
        for (int i = 0; i < Math.min(fLow, fft.length / 2); i++) {
            fft[i * 2] = 0;
            fft[i*2 + 1] = 0;
        }

        for (int i = fHigh; i < fft.length / 2 ; i++) {
            fft[i*2] = 0;
            fft[i*2 + 1] = 0;
        }
    }

    private double[] absolute(double[] data) {
        double[] output = new double[data.length / 2];

        for (int i = 0; i < output.length; i++)
            output[i] = Math.sqrt(data[i * 2] * data[i * 2] + data[i * 2 + 1] * data[i * 2 + 1]);

        return output;
    }

    public double output(double sample) {
        count++;
        samples.add(0, sample);

        if (samples.size() > BACKLOG_SIZE) samples.remove(samples.size() - 1);

        if (samples.size() != BACKLOG_SIZE) return sample;

        double[] fft = new double[samples.size() * 2];
        for (int i = 0; i < samples.size(); i++) fft[i] = samples.get(i);
        DoubleFFT_1D transform = new DoubleFFT_1D(samples.size());
        transform.realForward(fft);

        if (!graphed && count == 1333) {
            ArrayList<XYDataItem> data = new ArrayList<>();
            for (int i = 0; i < 25; i++) {
                data.add(new XYDataItem(i, fft[i]));
            }

            Plotter.plot("Frequency", "../assets/prefilter.png", "f", "a", new XYDataItem(1600, 900), data);
            filter(fft);

            data = new ArrayList<>();
            for (int i = 0; i < 25; i++) {
                data.add(new XYDataItem(i, fft[i]));
            }
            Plotter.plot("Frequency", "../assets/filter.png", "f", "a", new XYDataItem(1600, 900), data);
            transform.realInverseFull(fft, true);
            //fft = absolute(fft);

            data = new ArrayList<>();
            for (int i = 0; i < fft.length; i++) {
                data.add(new XYDataItem(i, fft[i]));
            }
            Plotter.plot("Frequency", "../assets/filterout.png", "f", "a", new XYDataItem(1600, 900), data);

            graphed = true;
        } else {
            filter(fft);
            transform.realInverseFull(fft, true);
            //fft = absolute(fft);
        }

        return fft[fft.length - 2];
    }

    public Filter(int fLow, int fHigh) {
        this.fLow = fLow;
        this.fHigh = fHigh;
    }
}
