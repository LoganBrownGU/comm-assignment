package sender;

public abstract class Modulator {

    private Datastream datastream;
    abstract double output(double t);

    public Modulator(Datastream datastream) {
        this.datastream = datastream;
    }

    public Datastream getDatastream() {
        return datastream;
    }
}
