package channel;

import transmitter.Filter;

public class Channel {
    private Filter filter;

    double output(double t) {
        
    }

    public Channel(Filter filter) {
        this.filter = filter;
    }

    public Channel() {
        this.filter = null;
    }
}
