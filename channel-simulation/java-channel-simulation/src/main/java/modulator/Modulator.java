package modulator;

import util.Buffer;

public abstract class Modulator {
    protected final float carrierFrequency, modulationFrequency, carrierAmplitude;
    public static final String[] parameters = {"carrier frequency", "modulation frequency", "carrier amplitude"};
    public static final String[] parameterDefaults = {"100000", "50000", "10"};
    public final Buffer buffer = new Buffer();

    public abstract float[] calculate(byte[] data, float timeStep);

    public abstract float getRMS();

    public Modulator(float carrierFrequency, float modulationFrequency, float carrierAmplitude) {
        this.carrierFrequency = carrierFrequency;
        this.modulationFrequency = modulationFrequency;
        this.carrierAmplitude = carrierAmplitude;
    }

    public float getCarrierFrequency() {
        return this.carrierFrequency;
    }

    public float getModulationFrequency() {
        return this.modulationFrequency;
    }

    public float getCarrierAmplitude() {
        return this.carrierAmplitude;
    }
}
