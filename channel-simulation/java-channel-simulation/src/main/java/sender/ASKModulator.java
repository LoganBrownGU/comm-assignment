package sender;

public class ASKModulator extends Modulator {

    private final double depth, amplitude, carrierF, shiftPeriod;
    private byte currentByte;
    private byte bitMask = 0b00000001;
    private int transitions;

    @Override
    public double output(double t) {
        // find the current bit frame that the system should be sending.
        int frame = (int) Math.floor(t / shiftPeriod);
        // if the number of bit transitions does not match the current frame, then move to the
        // next bit.
        if (transitions != frame) {
            bitMask <<= 1;
            transitions++;
        }
        // if the bitMask is 8 then reset it to 0 and request the next byte to be sent.
        if (bitMask == 0) {
            bitMask = 0b00000001;
            currentByte = super.getDatastream().nextByte();
        }

        boolean bit = (bitMask & currentByte) != 0;
        double f = amplitude * Math.sin(2 * Math.PI * carrierF * t);

        if (!bit) f *= 1 - depth;

        return f;
    }

    public ASKModulator(double depth, double amplitude, double carrierF, double modulationF, Datastream datastream) {
        super(datastream);
        this.depth = depth;
        this.amplitude = amplitude;
        this.carrierF = carrierF;
        this.shiftPeriod = 1 / modulationF;

        this.currentByte = datastream.nextByte();
    }
}
