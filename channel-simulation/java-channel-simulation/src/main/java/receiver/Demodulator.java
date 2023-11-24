package receiver;

import java.util.ArrayList;

public abstract class Demodulator {
    private ArrayList<Observer> observers = new ArrayList<>();
    private byte currentByte;

    protected void notifyObservers() {
        for (Observer o : observers)
            o.notifyObserver();
    }
    public abstract void receive(double f, double t);
}
