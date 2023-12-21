package receiver;

import java.util.ArrayList;

public abstract class Demodulator {
    private final ArrayList<Byte> receivedBytes = new ArrayList<>();
    private final DataOut dataOut;

    public abstract void receive(ArrayList<Double> samples, double start, double end, double step);

    public byte updateByte(byte currentByte, byte bitMask, boolean bit) {
        // bitMask only has 1 bit set, so an OR operation will set the same bit of currentByte to 1, and leave all others
        // untouched.
        if (bit) currentByte |= bitMask;
        else {
            // e.g. 0110 AND 0100 = 0100    0110 XOR 0100 = 0010
            // e.g. 0010 AND 0100 = 0000    0010 XOR 0000 = 0010
            byte invertMask = (byte) (currentByte & bitMask);
            currentByte ^= invertMask;
        }

        // if bitMask == 10000000 then a full byte has been read
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
