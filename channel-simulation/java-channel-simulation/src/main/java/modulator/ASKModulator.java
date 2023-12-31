package modulator;

public class ASKModulator extends Modulator {

    private final float depth;
    public static final String[] parameters = {"depth"};
    public static final String[] parameterDefaults = {"0.8"};

    @Override
    public float[] calculate(byte[] data, float timeStep) {
        float bitPeriod = 1f / this.modulationFrequency;
        float endTime = bitPeriod * data.length * 8;
        float[] amp = new float[(int) (endTime / timeStep)];

        for (int i = 0; i < amp.length; i++) {
            float t = i * timeStep;
            int bitFrame = (int) (t / bitPeriod);
            // select the byte
            int bitIndex = bitFrame / 8;
            if (bitIndex == data.length) break;

            // select the bit in the byte
            byte bitMask = (byte) (0b00000001 << (bitFrame % 8));
            boolean bit = (bitMask & data[bitIndex]) != 0;

            amp[i] = (float) (this.carrierAmplitude * Math.sin(2 * Math.PI * t * this.carrierFrequency) * (bit ? 1 : 1 - this.depth));
        }

        this.buffer.addData(data);

        return amp;
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
