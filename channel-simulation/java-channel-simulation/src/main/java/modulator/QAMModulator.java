package modulator;

public class QAMModulator extends Modulator {

    private final float order;

    public static final String[] parameters = {"order"};
    public static final String[] parameterDefaults = {"4"};

    private float inphase(float t) {
        return (float) Math.sin(2 * Math.PI * this.carrierFrequency * t);
    }

    private float quadrature(float t) {
        return (float) Math.cos(2 * Math.PI * this.carrierFrequency * t);
    }

    @Override
    public float[] calculate(byte[] data, float timeStep) {
        float symbolFrequency = 1f / this.modulationFrequency;
        int bitsPerSymbol = (int) (Math.log(this.order) / Math.log(2));
        float endTime = data.length * 8 * (symbolFrequency / bitsPerSymbol);
        int levels = (int) Math.sqrt(bitsPerSymbol);

        // Convert array of bytes into array of bits
        boolean[] bits = new boolean[data.length * 8];
        for (int i = 0; i < bits.length; i++) {
            byte b = data[i/8];
            byte bitMask = (byte) (0x01 << (i % 8));
            bits[i] = (b & bitMask) != 0;
        }

        float[] samples = new float[(int) (endTime / timeStep)];
        for (int i = 0; i < samples.length; i++) {
            float t = i * timeStep;
            // Find index of current symbol being sent.
            int symbolFrame = (int) (t / symbolFrequency);
            // Find index of first bit in symbol.
            int bitIndex = symbolFrame * bitsPerSymbol;

            // Construct symbol to be sent from bit array.
            int symbol = 0;
            for (int j = 0; j < bitsPerSymbol; j++)
                symbol = (symbol << 1) + (bits[bitIndex + bitsPerSymbol] ? 1 : 0);

            float f;
            // If symbol is even then use inphase.
            if (symbol % 2 == 0)    f = inphase(t);
            else                    f = quadrature(t);

            f *= this.carrierAmplitude / (float) (levels / (symbol / 2));
            samples[i] = f;
        }

        return samples;
    }

    @Override
    public float getRMS() {
        return (float) (this.carrierAmplitude / Math.sqrt(2));
    }

    public QAMModulator(float carrierFrequency, float modulationFrequency, float carrierAmplitude, float order) {
        super(carrierFrequency, modulationFrequency, carrierAmplitude);
        this.order = order;
    }
}
