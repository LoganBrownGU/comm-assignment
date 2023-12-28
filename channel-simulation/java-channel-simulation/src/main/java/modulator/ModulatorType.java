package modulator;

public enum ModulatorType {
    ASK,
    FSK,
    QAM;

    public static String[] getParameters(ModulatorType type) {
        String[] defaultParams = Modulator.parameters;
        String[] typeParams = {};

        // TODO add cases for other modulators
        switch (type) {
            case ASK:
                typeParams = ASKModulator.parameters;
                break;
            case FSK:
                break;
            case QAM:
                break;
        }

        String[] out = new String[defaultParams.length + typeParams.length];
        System.arraycopy(defaultParams, 0, out, 0, defaultParams.length);
        System.arraycopy(typeParams, 0, out, defaultParams.length, typeParams.length);

        return out;
    }
}
