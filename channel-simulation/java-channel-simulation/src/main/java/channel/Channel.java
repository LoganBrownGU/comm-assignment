package channel;

import main.Filter;

import java.util.ArrayList;
import java.util.Random;

public class Channel {
    private Filter filter;
    private final double noiseRMS;

    public ArrayList<Double> calculate(ArrayList<Double> samples) {
        ArrayList<Double> output = new ArrayList<>(samples);

        if (this.filter != null) return this.filter.calculate(output);

        return output;
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
