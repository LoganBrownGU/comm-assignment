package modulator;

import demodulator.ASKDemodulator;
import demodulator.Demodulator;

import java.util.ArrayList;

public class ModulatorFactory {

    public static ASKModulator createASKModulator(ArrayList<String> parameters) {
        float carrierFrequency = Float.parseFloat(parameters.get(0));
        float modulationFrequency = Float.parseFloat(parameters.get(1));
        float carrierAmplitude = Float.parseFloat(parameters.get(2));
        float depth = Float.parseFloat(parameters.get(3));

        return new ASKModulator(carrierFrequency, modulationFrequency, carrierAmplitude, depth);
    }

    public static Demodulator getDemodulator(Modulator modulator) {
        if (modulator instanceof ASKModulator) {
            ASKModulator ask = (ASKModulator) modulator;
            return new ASKDemodulator(ask.getDepth(), ask.getCarrierAmplitude(), ask.getModulationFrequency());
        }

        return null;
    }
}
