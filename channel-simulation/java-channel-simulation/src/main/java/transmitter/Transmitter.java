package transmitter;

import main.Filter;

public class Transmitter {

    private Modulator modulator;
    private Filter filter;

    double send(double t) {
        double f = modulator.output(t);
        if (filter != null) f = filter.output(f);

        return f;
    }

    public Transmitter(Modulator modulator) {
        this.modulator = modulator;
        filter = null;
    }

    public Transmitter(Filter filter) {
        this.filter = filter;
    }

    public void setModulator(Modulator modulator) {
        this.modulator = modulator;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}
