package modulator;

public class ASKModulator extends Modulator {

    private final float depth;
    public static final String[] parameters = {"depth"};

    @Override
    public float[] calculate(byte[] data, float timeStep) {
        float bitPeriod = 1 / this.modulationFrequency;
        float endTime = bitPeriod * data.length * 8;
        System.out.println(endTime/timeStep);
        float[] amp = new float[(int) (endTime / timeStep)];

        int i = 0;
        for (float t = 0; i < amp.length; t += timeStep, i++) {
            int bitFrame = (int) (t / bitPeriod);
            // select the byte
            int bitIndex = bitFrame / 8;
            // select the bit in the byte
            byte bitMask = (byte) (0b00000001 << (bitFrame % 8));
            boolean bit = (bitMask & data[bitIndex]) != 0;

            amp[i] = (float) (this.carrierAmplitude * Math.sin(2 * Math.PI * t * this.carrierFrequency) * (bit ? 1 : 1 - this.depth));
        }

        this.buffer.addData(data);

        return amp;
    }

    public ASKModulator(float carrierFrequency, float modulationFrequency, float carrierAmplitude, float depth) {
        super(carrierFrequency, modulationFrequency, carrierAmplitude);
        this.depth = depth;
    }
}
