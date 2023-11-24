package transmitter;

public abstract class Modulator {

    private Datastream datastream;
    abstract double output(double t);
    abstract double getRMS();

    public Modulator(Datastream datastream) {
        this.datastream = datastream;
    }

    public Datastream getDatastream() {
        return datastream;
    }
}
