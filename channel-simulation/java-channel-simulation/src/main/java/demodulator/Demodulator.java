package demodulator;

public abstract class Demodulator {

    protected float[] amp;
    protected int index = 0;

    public abstract void initialCalculate(float[] amp, float timeStep);

    public abstract void next();

    public void reset() {
        this.index = 0;
    }
}
