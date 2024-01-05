package util;

import java.util.ArrayList;

public class Buffer {
    /*
      Thread safe buffer.
      Multiple threads can attempt to access this buffer at the same time.
      If a thread tries to pull more data out of the buffer than the buffer contains, the thread will be halted until
      the buffer contains enough data.
    */

    private final ArrayList<Byte> contents = new ArrayList<>();

    public byte[] getChunk(int size) {
        synchronized (this) {
            while (this.contents.size() < size) {
                try {
                    this.wait();
                } catch (InterruptedException e) { throw new RuntimeException(e); }
            }

            byte[] chunk = new byte[size];
            for (int i = 0; i < size; i++) chunk[i] = this.contents.remove(0);
            this.notifyAll();
            return chunk;
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

    public int getSize() {
        return this.contents.size();
    }

    public boolean isEmpty() {
        return this.contents.isEmpty();
    }

}
