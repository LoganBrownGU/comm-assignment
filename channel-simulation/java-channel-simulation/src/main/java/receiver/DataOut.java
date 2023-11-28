package receiver;

import java.util.ArrayList;

public class DataOut {
    private final Object mutex = new Object();
    private final ArrayList<Byte> buffer = new ArrayList<>();
    private boolean closed = false;

    public void push(byte b) {
        synchronized (mutex) {
            this.buffer.add(b);
            mutex.notifyAll();
        }
    }

    public byte pop() throws InterruptedException {
        synchronized (mutex) {
            while (this.buffer.isEmpty() && !closed) mutex.wait();

            if (closed) return 0;
            else        return this.buffer.remove(0);
        }
    }

    public void close() {
        this.closed = true;
        synchronized (mutex) {
            mutex.notifyAll();
        }
    }

    public boolean isClosed() {
        return closed;
    }
}
