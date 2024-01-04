package modulator;

import util.Buffer;
import util.Filter;

public abstract class Modulator {
    protected final float carrierFrequency, modulationFrequency, carrierAmplitude;
    protected final Filter outputFilter;

    // Array of default parameters for use by SimulatorSettings class - all concrete Modulators will have these.
    public static final String[] parameters = {"carrier frequency", "modulation frequency", "carrier amplitude"};
    // Array of default values for each parameter.
    public static final String[] parameterDefaults = {"1000000", "100000", "100"};
    public final Buffer buffer = new Buffer();

    public abstract float[] calculate(byte[] data, float timeStep);

    public abstract float getRMS();

    public Modulator(float carrierFrequency, float modulationFrequency, float carrierAmplitude) {
        this.carrierFrequency = carrierFrequency;
        this.modulationFrequency = modulationFrequency;
        this.carrierAmplitude = carrierAmplitude;
        // Limit bandwidth to 20% of carrier frequency.
        this.outputFilter = new Filter((int) (carrierFrequency * 0.9f), (int) (carrierFrequency * 1.1f));
    }

    public Modulator(float carrierFrequency, float modulationFrequency, float carrierAmplitude, Filter outputFilter) {
        this.carrierFrequency = carrierFrequency;
        this.modulationFrequency = modulationFrequency;
        this.carrierAmplitude = carrierAmplitude;
        this.outputFilter = outputFilter;
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
