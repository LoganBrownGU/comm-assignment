package receiver;

import java.util.ArrayList;

public abstract class Demodulator {
    private final ArrayList<Byte> receivedBytes = new ArrayList<>();
    private final DataOut dataOut;

    public abstract void receive(ArrayList<Double> samples, double start, double end, double step);

    public byte updateByte(byte currentByte, byte bitMask, boolean bit) {
        if (bit) currentByte |= bitMask;
        else {
            byte invertMask = (byte) (currentByte & bitMask);
            currentByte ^= invertMask;
        }

        if (bitMask == (byte) 0b10000000) {
            this.dataOut.push(currentByte);
            this.receivedBytes.add(currentByte);
        }

        return currentByte;
    }

    public Demodulator(DataOut dataOut) {
        this.dataOut = dataOut;
    }

    public ArrayList<Byte> getReceivedBytes() {
        return this.receivedBytes;
    }

    public DataOut getDataOut() {
        return this.dataOut;
    }
}
