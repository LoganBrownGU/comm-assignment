package demodulator;

public class ASKDemodulator extends Demodulator {

    private final float depth, amplitude, modulationPeriod;
    private float maxAmp = 0;       // Max. amplitude for a given bit frame.
    private int transitions = 0;    // No. bit frame transitions.
    private byte bitMask = 0x01;    // This determines the bit being received for the current byte.
                                    // For example, if the 3rd least significant bit was being received,
                                    // then bitMask = 0000 0100

    @Override
    public void initialCalculate(float[] samples, float timeStep) {
        this.samples = samples;
    }


    // Noise is the actual random gaussian value, not the noise RMS, or SNR.
    @Override
    public void next(float noise) {
        float t = this.timeStep * this.index;
        float f = this.samples[this.index] + noise;

        this.maxAmp = Math.max(f, this.maxAmp);
        // Get the  current bit frame, i.e. the index of the current bit being received.
        int frame = (int) (t / this.modulationPeriod);

        // If the number of bit frame transitions does not equal the current frame, then move to the next frame.
        if (this.transitions != frame) {
            // Take the max amplitude for this frame check if it is greater than halfway between the "high" amplitude and
            // the "low" amplitude.
            super.updateByte(this.bitMask, this.maxAmp > this.amplitude * (1 - this.depth/2));

            this.maxAmp = 0;
            // Left shift bitMask by 1 bit.
            this.bitMask <<= 1;
            this.transitions++;

            // Left shifting 1000 0000 will give 0000 0000, so that means a full byte has been received and bitMask needs
            // to be reset.
            if (this.bitMask == (byte) 0b0000_0000) this.bitMask = 0b0000_0001;
        }

        this.index++;
    }

    public ASKDemodulator(float[] samples, float timeStep, float depth, float amplitude, float modulationFrequency) {
        super(samples, timeStep);
        this.depth = depth;
        this.amplitude = amplitude;
        this.modulationPeriod = 1f / modulationFrequency;
    }

    @Override
    public void reset() {
        super.reset();
        this.bitMask = 0b0000_0001;
        this.transitions = 0;
    }
}