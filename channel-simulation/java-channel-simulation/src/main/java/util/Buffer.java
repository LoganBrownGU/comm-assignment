package util;

import java.util.ArrayList;

public class Buffer {
    /*
    * Thread safe buffer.
    * Multiple threads can attempt to access this buffer at the same time.
    * If a thread tries to pull more data out of the buffer than the buffer contains, the thread will be halted until
      the buffer contains enough data.
    */

    private final ArrayList<Byte> contents = new ArrayList<>();
    private boolean open = true;

    public byte[] getChunk(int size) {
        synchronized (this) {
            while (this.contents.size() < size && this.open) {
                try {
                    this.wait();
                } catch (InterruptedException e) { throw new RuntimeException(e); }
            }
            if (!this.open) return null;

            byte[] chunk = new byte[size];
            for (int i = 0; i < size; i++) chunk[i] = this.contents.remove(0);
            this.notifyAll();
            return chunk;
        }
    }

    public byte getByte() throws InterruptedException {
        synchronized (this) {
            while (this.contents.isEmpty() && this.open) this.wait();
            if (!this.open) return (byte) 0xFF;

            return this.contents.remove(0);
        }
    }

    public void addData(byte b) {
        synchronized (this) {
            this.contents.add(b);
            this.notifyAll();
        }
    }

    public void addData(byte[] data) {
        synchronized (this) {
            for (byte b : data) this.contents.add(b);
            this.notifyAll();
        }
    }

    public void clear() {
        synchronized (this) {
            this.contents.clear();
            this.notifyAll();
        }
    }

    public boolean isEmpty() {
        return this.contents.isEmpty();
    }

    public void close() {
        this.open = false;
    }

    public boolean isClosed() {
        return !this.open;
    }
}
