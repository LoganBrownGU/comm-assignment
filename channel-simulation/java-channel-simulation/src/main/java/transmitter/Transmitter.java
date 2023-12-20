package transmitter;

import main.Filter;

import java.util.ArrayList;

public class Transmitter {

    private Modulator modulator;
    private Filter filter;

    public ArrayList<Double> calculate(double start, double end, double step) {
        ArrayList<Double> output = new ArrayList<>();
        for (double t = start; t < end; t += step) {
            double f = this.modulator.output(t);
            output.add(f);
        }
        if (this.filter != null) output = this.filter.calculate(output);

        return output;
    }

    public Transmitter(Modulator modulator) {
        this.modulator = modulator;
        this.filter = null;
    }

    public Transmitter(Modulator modulator, Filter filter) {
        this.modulator = modulator;
        this.filter = filter;
    }

    public void setModulator(Modulator modulator) {
        this.modulator = modulator;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public byte getCurrentByte() {
        if (this.modulator.getSentBytes().isEmpty()) return (byte) 0xFF;
        else return this.modulator.getSentBytes().get(this.modulator.getSentBytes().size() - 1);
    }

    public Modulator getModulator() {
        return this.modulator;
    }
}
