package modulator;

import util.Filter;
import util.Maths;

public class QAMModulator extends Modulator {

    private final float order;

    public static final String[] parameters = {"order"};
    public static final String[] parameterDefaults = {"16"};

    private float inphase(float t) {
        return (float) Math.sin(2 * Math.PI * this.carrierFrequency * t);
    }

    private float quadrature(float t) {
        return (float) Math.cos(2 * Math.PI * this.carrierFrequency * t);
    }

    @Override
    public float[] calculate(byte[] data, float timeStep) {
        float symbolPeriod = 1f / this.modulationFrequency;
        int bitsPerSymbol = (int) Maths.log2(this.order);
        float endTime = data.length * 8 * (symbolPeriod / bitsPerSymbol);
        int levels = (int) Math.pow(2, bitsPerSymbol / 2);

        // Convert array of bytes into array of bits
        boolean[] bits = new boolean[data.length * 8];
        for (int i = 0; i < bits.length-1; i++) {
            byte b = data[i/8];
            byte bitMask = (byte) (0x01 << (i % 8));
            bits[i] = (b & bitMask) != 0;
        }

        float[] samples = new float[(int) (endTime / timeStep)];
        for (int i = 0; i < samples.length; i++) {
            float t = i * timeStep;
            // Find index of current symbol being sent.
            int symbolFrame = (int) (t / symbolPeriod);
            // Find index of first bit in symbol.
            int bitIndex = symbolFrame * bitsPerSymbol;
            // Floating point arithmetic often causes the sample count to be slightly too long, so break if the byteIndex
            // would cause an index out of bounds error.
            if (bitIndex + bitsPerSymbol >= bits.length) break;

            // Construct symbol to be sent from bit array.
            long symbol = 0;
            for (int j = 0; j < bitsPerSymbol; j++)
                symbol = (symbol << 1) + (bits[bitIndex + j] ? 1 : 0);

            // E.g. 1111 1111 << 2 = 1111 1100.
            //      ~1111 1100 = 0000 0011.
            // -1 = 0xFFFFFFFFFFFFFFFF
            long bitMask = (byte) ~(-1 << (bitsPerSymbol / 2));
            float aI = inphase(t) * (this.carrierAmplitude) * (symbol & bitMask);
            symbol >>= bitsPerSymbol / 2;
            float aQ = quadrature(t) * (this.carrierAmplitude) * (symbol & bitMask);

            samples[i] = aI + aQ;
        }
        this.outputFilter.filter(samples, timeStep);

        this.buffer.addData(data);

        return samples;
    }

    @Override
    public float getRMS() {
        return (float) (this.carrierAmplitude / Math.sqrt(2));
    }

    public QAMModulator(float carrierFrequency, float modulationFrequency, float carrierAmplitude, float order, Filter filter) {
        super(carrierFrequency, modulationFrequency, carrierAmplitude, filter);
        this.order = order;
    }

    public float getOrder() {
        return this.order;
    }
}
