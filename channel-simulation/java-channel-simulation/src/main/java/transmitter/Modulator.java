package transmitter;

import java.util.ArrayList;

public abstract class Modulator {

    private DataStream datastream;
    private ArrayList<Byte> sentBytes = new ArrayList<>();

    abstract double output(double t);
    abstract double getRMS();

    public Modulator(DataStream datastream) {
        this.datastream = datastream;
    }

    public DataStream getDatastream() {
        return datastream;
    }

    public void setDatastream(DataStream datastream) {
        this.datastream = datastream;
    }

    protected void addByte(byte b) {
        sentBytes.add(b);
    }

    public ArrayList<Byte> getSentBytes() {
        return sentBytes;
    }
}
