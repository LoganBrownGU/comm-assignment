package transmitter;

public class ASKModulator extends Modulator {

    private final double depth, amplitude, carrierF, shiftPeriod;
    private byte currentByte;
    private byte bitmask = 0b00000001;
    private int transitions;

    @Override
    public double output(double t) {
        // find the current bit frame that the system should be sending.
        int frame = (int) Math.floor(t / this.shiftPeriod);
        // if the number of bit transitions does not match the current frame, then move to the
        // next bit.
        if (this.transitions != frame) {
            this.bitmask <<= 1;
            this.transitions++;
        }
        // if the bitMask is 8 then reset it to 0 and request the next byte to be sent.
        if (this.bitmask == 0) {
            this.bitmask = 0b00000001;
            this.currentByte = super.getDatastream().nextByte();
            super.addByte(this.currentByte);
        }

        boolean bit = (this.bitmask & this.currentByte) != 0;
        double f = this.amplitude * Math.sin(2 * Math.PI * this.carrierF * t);

        if (!bit) f *= 1 - depth;

        return f;
    }

    @Override
    public double getRMS() {
        return 0.707 * amplitude;
    }

    public ASKModulator(double depth, double amplitude, double carrierF, double modulationF, DataStream datastream) {
        super(datastream);
        this.depth = depth;
        this.amplitude = amplitude;
        this.carrierF = carrierF;
        this.shiftPeriod = 1 / modulationF;

        this.currentByte = datastream.nextByte();
        super.addByte(this.currentByte);
    }

    // Only for use by simulation display
    public byte peekByte() {
        return this.currentByte;
    }
}
