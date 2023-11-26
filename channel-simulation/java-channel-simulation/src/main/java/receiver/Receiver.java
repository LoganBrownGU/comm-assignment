package receiver;

import main.Filter;

public class Receiver {

    private Filter filter;
    private Demodulator demodulator;
    private final DataOut dataOut;

    private static class DataOut implements Observer {
        @Override
        public void notifyThis(byte value) {

        }
    }

    public void receive(double f, double t) {
        if (filter != null) f = filter.filter(f);
        demodulator.receive(f, t);
    }

    public Receiver(Filter filter, Demodulator demodulator) {
        this.filter = filter;
        this.demodulator = demodulator;
        dataOut = new DataOut();
        demodulator.addObserver(dataOut);
    }

    public Receiver(Demodulator demodulator) {
        this.filter = null;
        this.demodulator = demodulator;
        dataOut = new DataOut();
        demodulator.addObserver(dataOut);
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
}
