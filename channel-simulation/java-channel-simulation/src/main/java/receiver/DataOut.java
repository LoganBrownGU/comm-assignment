package receiver;

import java.util.ArrayList;

public class DataOut {
    private final Object mutex = new Object();
    private final ArrayList<Byte> buffer = new ArrayList<>();
    private boolean closed = false;

    public void push(byte b) {
        synchronized (mutex) {
            this.buffer.add(b);
            mutex.notify();
        }
    }

    public byte pop() throws InterruptedException {
        synchronized (mutex) {
            while (this.buffer.isEmpty()) mutex.wait();
            return this.buffer.remove(0);
        }
    }

    public void close() {
        this.closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
