package main;

import java.util.ArrayList;

public class Filter {
    private final double fMin, fMax;
    private final int span;
    private final ArrayList<Double> samples = new ArrayList<>();

    public double filter(double sample) {
        double alpha = 0.1;
        double output = alpha * sample + (1-alpha) * this.samples.get(0);
        this.samples.add(output);
        if (this.samples.size() > this.span) this.samples.remove(0);

        return output;
    }

    public Filter(double fMin, double fMax, int span) {
        this.fMin = 1/fMin;
        this.fMax = 1/fMax;
        this.span = span;
        this.samples.add(0d);
    }
}
