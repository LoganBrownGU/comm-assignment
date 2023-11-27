package channel;

import main.Filter;

import java.util.Random;

public class Channel {
    private Filter filter;
    private final Random random = new Random();
    private final double noiseRMS;

    public double output(double f) {
        f += random.nextGaussian() * noiseRMS;

        if (filter != null) return filter.output(f);
        else return f;
    }

    public Channel(Filter filter, double signalRMS, double noiseLevel) {
        this.noiseRMS = Math.pow(10, -noiseLevel / 20) * signalRMS;
        this.filter = filter;
    }

    public Channel(double signalRMS, double noiseLevel) {
        this.noiseRMS = Math.pow(10, -noiseLevel / 20) * signalRMS;
        this.filter = null;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
