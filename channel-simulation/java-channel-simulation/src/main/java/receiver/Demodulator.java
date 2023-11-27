package receiver;

import java.util.ArrayList;

public abstract class Demodulator {
    private final ArrayList<Byte> receivedBytes = new ArrayList<>();
    private byte currentByte;
    private final DataOut dataOut;

    public abstract void receive(double f, double t);

    public void updateByte(byte bitMask, boolean bit) {
        if (bit) this.currentByte |= bitMask;
        else {
            byte invertMask = (byte) (currentByte & bitMask);
            currentByte ^= invertMask;
        }

        if (bitMask == (byte) 0b10000000) {
            dataOut.push(currentByte);
            receivedBytes.add(currentByte);
        }
    }

    public Demodulator(DataOut dataOut) {
        this.dataOut = dataOut;
    }

    public ArrayList<Byte> getReceivedBytes() {
        return receivedBytes;
    }

    public DataOut getDataOut() {
        return dataOut;
    }
}
