package modulator;

import java.util.ArrayList;

public class ModulatorFactory {

    public static ASKModulator createASKModulator(ArrayList<String> parameters) {
        double carrierFrequency = Double.parseDouble(parameters.get(0));
        double modulationFrequency = Double.parseDouble(parameters.get(1));
        double carrierAmplitude = Double.parseDouble(parameters.get(2));
        double depth = Double.parseDouble(parameters.get(3));

        return new ASKModulator(carrierFrequency, modulationFrequency, carrierAmplitude, depth);
    }
}
