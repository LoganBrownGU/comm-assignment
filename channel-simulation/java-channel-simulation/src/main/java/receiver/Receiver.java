package receiver;

import main.Filter;

import java.util.ArrayList;
import java.util.Arrays;

public class Receiver {

    private Filter filter;
    private Demodulator demodulator;

    public void receive(ArrayList<Double> samples, double start, double end, double step) {
        ArrayList<Double> output = new ArrayList<>(samples);

        if (this.filter != null) output = this.filter.calculate(samples);
        this.demodulator.receive(output, start, end, step);
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
