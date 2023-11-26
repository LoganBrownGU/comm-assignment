package receiver;

public class ASKDemodulator extends Demodulator {
    private final double depth, amplitude, carrierF, modulationPeriod;
    private double maxAmp;          // max amplitudes for the current time frame.
    private int transitions;        // number of time frame transitions
    private byte bitMask = 0b00000001;

    @Override
    public void receive(double f, double t) {
        this.maxAmp = Math.max(this.maxAmp, f);
        int frame = (int) Math.floor(t / this.modulationPeriod);
        // if the number of bit transitions does not match the current frame, then move to the
        // next bit.
        if (this.transitions != frame) {
            updateByte(this.bitMask, maxAmp > this.amplitude * (1 - this.depth / 2));

            this.maxAmp = 0;
            this.bitMask <<= 1;
            this.transitions++;
        }

        if (this.bitMask == (byte) 0b00000000) this.bitMask = 0b00000001;
    }

    public ASKDemodulator(double depth, double carrierAmp, double carrierF, double modulationF) {
        this.depth = depth;
        this.amplitude = carrierAmp;
        this.carrierF = carrierF;
        this.modulationPeriod = 1 / modulationF;
    }
}
