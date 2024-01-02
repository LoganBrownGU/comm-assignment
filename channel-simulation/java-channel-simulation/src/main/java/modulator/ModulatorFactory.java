package modulator;

import demodulator.ASKDemodulator;
import demodulator.Demodulator;
import demodulator.QAMDemodulator;

import java.util.ArrayList;

public class ModulatorFactory {

    public static ASKModulator createASKModulator(ArrayList<String> parameters) {
        float carrierFrequency = Float.parseFloat(parameters.get(0));
        float modulationFrequency = Float.parseFloat(parameters.get(1));
        float carrierAmplitude = Float.parseFloat(parameters.get(2));
        float depth = Float.parseFloat(parameters.get(3));

        return new ASKModulator(carrierFrequency, modulationFrequency, carrierAmplitude, depth);
    }

    public static QAMModulator createQAMModulator(ArrayList<String> parameters) {
        float carrierFrequency = Float.parseFloat(parameters.get(0));
        float modulationFrequency = Float.parseFloat(parameters.get(1));
        float carrierAmplitude = Float.parseFloat(parameters.get(2));
        float order = Integer.parseInt(parameters.get(3));

        return new QAMModulator(carrierFrequency, modulationFrequency, carrierAmplitude, order);
    }

    public static Demodulator getDemodulator(Modulator modulator, float[] samples, float timeStep) {
        if (modulator instanceof ASKModulator) {
            ASKModulator ask = (ASKModulator) modulator;
            return new ASKDemodulator(samples, timeStep, ask.getDepth(), ask.getCarrierAmplitude(), ask.getModulationFrequency());
        } else if (modulator instanceof QAMModulator) {
            QAMModulator qam = (QAMModulator) modulator;
            return new QAMDemodulator(samples, timeStep, qam.getCarrierAmplitude(), qam.getCarrierFrequency(), qam.getModulationFrequency(), qam.getOrder());
        }

        return null;
    }
}
