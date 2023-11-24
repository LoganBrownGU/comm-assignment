package sender;

public class Filter {
    private double fLow, fHigh;
    // number of samples to store
    private final int BACKLOG_SIZE = 1000;
    private final double[] samples = new double[BACKLOG_SIZE];

    public double filter(double sample) {

        return 0;
    }

    public Filter(double fLow, double fHigh) {
        this.fLow = fLow;
        this.fHigh = fHigh;
    }
}
