package modulator;

import util.Filter;

public class ASKModulator extends Modulator {

    private final float depth;
    // Additional parameters for ASK
    public static final String[] parameters = {"depth"};
    public static final String[] parameterDefaults = {"0.8"};

    @Override
    public float[] calculate(byte[] data, float timeStep) {
        // Amount of time each bit is transmitted for.
        float bitPeriod = 1f / this.modulationFrequency;
        float endTime = bitPeriod * data.length * 8;
        // Allocate array to store samples in.
        float[] samples = new float[(int) (endTime / timeStep)];

        for (int i = 0; i < samples.length; i++) {
            float t = i * timeStep;
            // Find index of current bit being sent
            int bitFrame = (int) (t / bitPeriod);
            // Find index of current byte being sent
            int byteIndex = bitFrame / 8;
            // Floating point arithmetic often causes the sample count to be slightly too long, so break if the byteIndex
            // would cause an index out of bounds error.
            if (byteIndex == data.length) break;

            // Select the bit in the byte
            byte bitMask = (byte) (0b00000001 << (bitFrame % 8));
            boolean bit = (bitMask & data[byteIndex]) != 0;

            samples[i] = (float) (this.carrierAmplitude * Math.sin(2 * Math.PI * t * this.carrierFrequency) * (bit ? 1 : 1 - this.depth));
        }
        this.outputFilter.filter(samples, timeStep);

        this.buffer.addData(data);

        return samples;
    }

    @Override
    public float getRMS() {
        return (float) (this.carrierAmplitude / Math.sqrt(2));
    }

    public ASKModulator(float carrierFrequency, float modulationFrequency, float carrierAmplitude, float depth) {
        super(carrierFrequency, modulationFrequency, carrierAmplitude);
        this.depth = depth;
    }

    public float getDepth() {
        return this.depth;
    }
}
