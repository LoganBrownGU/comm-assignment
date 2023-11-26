package receiver;

import java.util.ArrayList;

public abstract class Demodulator {
    private final ArrayList<Observer> observers = new ArrayList<>();
    private final ArrayList<Byte> receivedBytes = new ArrayList<>();
    private byte currentByte;

    protected void notifyObservers() {
        for (Observer o : observers)
            o.notifyThis(currentByte);
    }

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    public abstract void receive(double f, double t);

    public void updateByte(byte bitMask, boolean bit) {
        if (bit) this.currentByte |= bitMask;
        else {
            byte invertMask = (byte) (currentByte & bitMask);
            currentByte ^= invertMask;
        }

        if (bitMask == (byte) 0b10000000) {
            notifyObservers();
            receivedBytes.add(currentByte);
        }
    }

    public ArrayList<Byte> getReceivedBytes() {
        return receivedBytes;
    }
}
