package modulator;

public class ASKModulator extends Modulator {

    private final double depth;
    public static final String[] parameters = {"depth"};

    @Override
    public double[] calculate(byte[] data, double timeStep) {
        double bitPeriod = 1 / this.modulationFrequency;
        double endTime = bitPeriod * data.length * 8;
        double[] amp = new double[(int) (endTime / timeStep)];

        int i = 0;
        for (double t = 0; i < amp.length; t += timeStep, i++) {
            int bitFrame = (int) (t / bitPeriod);
            // select the byte
            int bitIndex = bitFrame / 8;
            // select the bit in the byte
            byte bitMask = (byte) (0b00000001 << (bitFrame % 8));
            boolean bit = (bitMask & data[bitIndex]) != 0;

            amp[i] = this.carrierAmplitude * Math.sin(2 * Math.PI * t * this.carrierFrequency) * (bit ? 1 : 1 - this.depth);
        }

        this.buffer.addData(data);

        return amp;
    }

    public ASKModulator(double carrierFrequency, double modulationFrequency, double carrierAmplitude, double depth) {
        super(carrierFrequency, modulationFrequency, carrierAmplitude);
        this.depth = depth;
    }
}
