package demodulator;

import display.SimulatorView;
import org.jfree.data.xy.XYDataItem;
import util.Filter;
import util.Maths;
import util.Plotter;

import java.util.ArrayList;

public class QAMDemodulator extends Demodulator {

    private final float carrierAmplitude, carrierFrequency, symbolPeriod, order;
    private final int samplesPerSymbolPeriod, levels, bitsPerSymbol;

    private float sumAmpI = 0;
    private float sumAmpQ = 0;
    private int transitions = 0;

    private float[] iSamples, qSamples;

    ArrayList<XYDataItem> data = new ArrayList<>();

    private float localOscillatorI(float t) {
        return (float) Math.sin(2 * Math.PI * this.carrierFrequency * t);
    }

    private float localOscillatorQ(float t) {
        return (float) Math.cos(2 * Math.PI * this.carrierFrequency * t);
    }

    private byte getVal(int levels) {
        // Find the average values for this symbol frame.
        float avgI = this.sumAmpI / this.samplesPerSymbolPeriod;
        float avgQ = this.sumAmpQ / this.samplesPerSymbolPeriod;

        // Find the integer value that the amplitude represents.
        int iVal = (byte) 0xFF, qVal = (byte) 0xFF;
        float div = this.carrierAmplitude / levels;
        for (byte i = 0; i < levels; i++) {
            float correctVal = i * div - 0.5f * div;
            if (avgI > correctVal - 0.5f * div && avgI < correctVal + 0.5f * div)   iVal = i;
            if (avgQ > correctVal - 0.5f * div && avgQ < correctVal + 0.5f * div)   qVal = i;
        }
        qVal <<= this.bitsPerSymbol / 2;

        byte val = (byte) (qVal + iVal);
        // Due to modulation, byte is reversed.
        return Maths.reverseByte(val);
    }

    @Override
    public void initialCalculate(float[] samples) {
        this.iSamples = new float[samples.length];
        this.qSamples = new float[samples.length];
        System.arraycopy(samples, 0, this.iSamples, 0, samples.length);
        System.arraycopy(samples, 0, this.qSamples, 0, samples.length);

        for (int i = 0; i < samples.length; i++) {
            float t = i * this.timeStep;
            this.iSamples[i] *= localOscillatorI(t);
            this.qSamples[i] *= localOscillatorQ(t);
        }

        Filter filter = new Filter(0, (int) Math.ceil(this.carrierFrequency * 1.5));
        filter.filter(this.iSamples, this.timeStep);
        filter.filter(this.qSamples, this.timeStep);

        this.samples = samples;
    }

    @Override
    public void next(float noise) {
        float t = this.index * this.timeStep;

        float aI = this.iSamples[this.index] + noise;
        float aQ = this.qSamples[this.index] + noise;

        if (index < 10 * this.samplesPerSymbolPeriod) {
            this.data.add(new XYDataItem(t, 2 * aI));
        } else if (!data.isEmpty()) {
            Plotter.plot("ishfsd", "assets/i.png", "t", "a", new XYDataItem(1600, 900), data);
            data.clear();
        }

        // After filtering, left with 1/2 Q(t) and 1/2 I(t)
        aI *= 2;
        aQ *= 2;
        this.sumAmpI += aI;
        this.sumAmpQ += aQ;

        int frame = (int) (t / this.symbolPeriod);
        if (this.transitions != frame) {
            this.transitions++;
            byte val = getVal(this.levels);

            this.currentByte <<= this.bitsPerSymbol;
            this.currentByte |= val;

            // For simplicity assume that bitsPerSymbol is an exact divisor of 8.
            if (this.bitsPerSymbol <= 8 && (this.transitions * this.bitsPerSymbol) % 8 == 0)
                this.buffer.addData(this.currentByte);

            this.sumAmpI = 0;
            this.sumAmpQ = 0;
        }

        this.index++;
    }

    public QAMDemodulator(float[] samples, float timeStep, float carrierAmplitude, float carrierFrequency, float modulationFrequency, float order) {
        super(samples, timeStep);
        this.carrierAmplitude = carrierAmplitude;
        this.carrierFrequency = carrierFrequency;
        this.symbolPeriod = 1f / modulationFrequency;
        this.order = order;
        this.samplesPerSymbolPeriod = (int) (this.symbolPeriod / timeStep);
        this.bitsPerSymbol = (int) Maths.log2(this.order);
        this.levels = (int) Math.pow(2, (double) this.bitsPerSymbol / 2);
    }

    @Override
    public void reset() {
        super.reset();
        this.transitions = 0;
        this.sumAmpI = 0;
        this.sumAmpQ = 0;
    }

    public float getCarrierFrequency() {
        return this.carrierFrequency;
    }
}
