package receiver;

import main.Filter;

public class Receiver {

    private Filter filter;
    private Demodulator demodulator;

    public void receive(double f, double t) {
        if (filter != null) f = filter.output(f);
        demodulator.receive(f, t);
    }

    public Receiver(Filter filter, Demodulator demodulator) {
        this.filter = filter;
        this.demodulator = demodulator;
    }

    public Receiver(Demodulator demodulator) {
        this.filter = null;
        this.demodulator = demodulator;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setDemodulator(Demodulator demodulator) {
        this.demodulator = demodulator;
    }

    public Demodulator getDemodulator() {
        return demodulator;
    }

    public byte getCurrentByte() {
        if (this.getDemodulator().getReceivedBytes().isEmpty()) return (byte) 0xFF;
        else return this.demodulator.getReceivedBytes().get(this.demodulator.getReceivedBytes().size() - 1);
    }
}
