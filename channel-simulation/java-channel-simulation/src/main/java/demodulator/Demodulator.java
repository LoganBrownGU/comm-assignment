package demodulator;

import util.Buffer;

public abstract class Demodulator {

    protected float[] amp;
    protected int index = 0;
    protected float timeStep;
    private byte currentByte;

    public final Buffer buffer = new Buffer();

    protected abstract void initialCalculate(float[] amp, float timeStep);

    public abstract void next(float noise);

    protected void updateByte(byte bitMask, boolean bit) {
        if (bit) this.currentByte |= bitMask;
        else {
            byte invertMask = (byte) (this.currentByte & bitMask);
            this.currentByte ^= invertMask;
        }

        if (bitMask == (byte) 0b10000000) this.buffer.addData(this.currentByte);
    }

    public void reset() {
        this.index = 0;
    }

    public Demodulator(float[] amp, float timeStep) {
        this.timeStep = timeStep;
        initialCalculate(amp, timeStep);
    }
}
