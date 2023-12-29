package demodulator;

import util.Buffer;

public abstract class Demodulator {

    public final Buffer buffer = new Buffer();

    protected abstract void calculate(float[] amp, float snr, float timeStep);

    protected byte updateByte(byte currentByte, byte bitMask, boolean bit) {
        if (bit) currentByte |= bitMask;
        else {
            byte invertMask = (byte) (currentByte & bitMask);
            currentByte ^= invertMask;
        }

        return currentByte;
    }
}
