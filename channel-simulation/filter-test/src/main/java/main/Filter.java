package main;

public class Filter {
    private final double fMin, fMax;
    private double prev;
    private int frame = 0;

    public double filter(double sample, double t) {
        double alpha = 0.1;
        double output = alpha * sample + (1-alpha) * prev;
        if ((int) (t / 0.1) != frame) {
            this.prev = sample;
            frame++;
        }
        return output;
    }

    public Filter(double fMin, double fMax) {
        this.fMin = 1/fMin;
        this.fMax = 1/fMax;
        this.prev = 0;
    }
}
