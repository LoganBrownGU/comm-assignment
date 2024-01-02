package demodulator;

import util.Buffer;

public abstract class Demodulator {

    protected float[] samples;
    protected int index = 0;
    protected float timeStep;
    protected byte currentByte;

    public final Buffer buffer = new Buffer();

    protected abstract void initialCalculate(float[] samples, float timeStep);

    public abstract void next(float noise);

    protected void updateByte(byte bitMask, boolean bit) {
        /*
        If bitMask = 0000 0100 and bit is true:
            currentByte = XXXX XXXX
                            OR
            bitMask     = 0000 0100
                          XXXX X1XX     (since X OR 1 is always 1, and X OR 0 is always X)

        If bitMask = 0000 0100 and bit is false:
            currentByte = XXXX XXXX
                            AND
            bitMask     = 0000 0100
            invertMask  = 0000 0X00     (since X AND 0 is always 0, and X AND 1 is always X)
                            XOR
            currentByte = XXXX XXXX
                          XXXX X0XX     (since X XOR X is always 0, and X XOR 0 is always X)

            So only the bit specified by bitMask is altered.
        */
        if (bit) this.currentByte |= bitMask;
        else {
            byte invertMask = (byte) (this.currentByte & bitMask);
            this.currentByte ^= invertMask;
        }

        // If bitMask = 1000 0000 then a full byte has been transmitted and should be added to buffer.
        if (bitMask == (byte) 0b1000_0000) this.buffer.addData(this.currentByte);
    }

    public void reset() {
        this.index = 0;
    }

    public Demodulator(float[] samples, float timeStep) {
        this.timeStep = timeStep;
        initialCalculate(samples, timeStep);
    }
}