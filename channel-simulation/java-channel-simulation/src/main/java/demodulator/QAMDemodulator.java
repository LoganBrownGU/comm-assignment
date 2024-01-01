package demodulator;

public class QAMDemodulator extends Demodulator {

    private final float carrierAmplitude, carrierFrequency, symbolPeriod, order;

    private float maxAmp = 0;
    private int transitions = 0;

    private float localOscillatorI(float t) {
        return (float) Math.sin(2 * Math.PI * this.carrierFrequency * t);
    }

    private float localOscillatorQ(float t) {
        return (float) Math.cos(2 * Math.PI * this.carrierFrequency * t);
    }

    @Override
    protected void initialCalculate(float[] samples, float timeStep) {
        this.samples = samples;
    }

    @Override
    public void next(float noise) {
        float t = this.index * this.timeStep;
        float f = this.samples[this.index] + noise;

        float inphase = f * localOscillatorI(t);
        float quadrature = f * localOscillatorQ(t);



        this.index++;
    }

    public QAMDemodulator(float[] samples, float timeStep, float carrierAmplitude, float carrierFrequency, float modulationFrequency, float order) {
        super(samples, timeStep);
        this.carrierAmplitude = carrierAmplitude;
        this.carrierFrequency = carrierFrequency;
        this.symbolPeriod = 1f / modulationFrequency;
        this.order = order;
    }

    @Override
    public void reset() {
        super.reset();
        this.transitions = 0;
        this.maxAmp = 0;    
    }
}
