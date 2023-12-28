package modulator;

import util.Buffer;

public abstract class Modulator {
    protected final double carrierFrequency, modulationFrequency, carrierAmplitude;
    public static final String[] parameters = {"carrier frequency", "modulation frequency", "carrier amplitude"};
    public final Buffer buffer = new Buffer();

    public abstract double[] calculate(byte[] data, double timeStep);

    public Modulator(double carrierFrequency, double modulationFrequency, double carrierAmplitude) {
        this.carrierFrequency = carrierFrequency;
        this.modulationFrequency = modulationFrequency;
        this.carrierAmplitude = carrierAmplitude;
    }
}
