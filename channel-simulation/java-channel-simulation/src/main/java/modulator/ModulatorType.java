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
                typeParams = QAMModulator.parameters;
        }

        String[] out = new String[defaultParams.length + typeParams.length];
        System.arraycopy(defaultParams, 0, out, 0, defaultParams.length);
        System.arraycopy(typeParams, 0, out, defaultParams.length, typeParams.length);

        return out;
    }

    public static String[] getParameterDefaults(ModulatorType type) {
        String[] defaultDefaults = Modulator.parameterDefaults;
        String[] typeDefaults = {};

        // TODO add cases for other modulators
        switch (type) {
            case ASK:
                typeDefaults = ASKModulator.parameterDefaults;
                break;
            case FSK:
                break;
            case QAM:
                typeDefaults = QAMModulator.parameterDefaults;
                break;
        }

        String[] out = new String[defaultDefaults.length + typeDefaults.length];
        System.arraycopy(defaultDefaults, 0, out, 0, defaultDefaults.length);
        System.arraycopy(typeDefaults, 0, out, defaultDefaults.length, typeDefaults.length);

        return out;
    }
}
