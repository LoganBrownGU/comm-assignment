package demodulator;

import org.jfree.data.xy.XYDataItem;

public class ASKDemodulator extends Demodulator {

    private final float depth, amplitude, modulationPeriod;
    private float maxAmp;       // max amplitude for this bit frame
    private int transitions;    // no. bit transitions
    private byte bitMask = 0x01;
    public XYDataItem[] data;

    @Override
    public void initialCalculate(float[] amp, float timeStep) {
        this.amp = amp;
        this.data = new XYDataItem[10000];
    }

    @Override
    public void next(float noise) {
        float time = this.timeStep * this.index;
        float f = this.amp[this.index] + noise;

        this.maxAmp = Math.max(f, this.maxAmp);
        int frame = (int) (time / this.modulationPeriod);

        if (this.transitions != frame) {
            if (transitions > 1000 && transitions < 1030) System.out.println(time);
            updateByte(this.bitMask, this.maxAmp > this.amplitude * (1 - this.depth/2));

            this.maxAmp = 0;
            this.bitMask <<= 1;
            this.transitions++;

            if (this.bitMask == (byte) 0x00) this.bitMask = 0x01;
        }

        this.index++;
    }

    public ASKDemodulator(float[] amp, float timeStep, float depth, float amplitude, float modulationFrequency) {
        super(amp, timeStep);
        this.depth = depth;
        this.amplitude = amplitude;
        this.modulationPeriod = 1f / modulationFrequency;
    }
}