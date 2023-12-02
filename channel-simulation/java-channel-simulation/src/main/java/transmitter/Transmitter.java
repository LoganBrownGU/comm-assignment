package transmitter;

import main.Filter;

public class Transmitter {

    private Modulator modulator;
    private Filter filter;

    public double send(double t) {
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

    public byte getCurrentByte() {
        if (this.modulator.getSentBytes().isEmpty()) return (byte) 0xFF;
        else return this.modulator.getSentBytes().get(this.modulator.getSentBytes().size() - 1);
    }
}
