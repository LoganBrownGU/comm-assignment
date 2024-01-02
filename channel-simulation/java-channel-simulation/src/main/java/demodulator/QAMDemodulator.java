package demodulator;

public class QAMDemodulator extends Demodulator {

    private final float carrierAmplitude, carrierFrequency, symbolPeriod, order;
    private final int samplesPerSymbolPeriod;

    private float sumAmpI = 0;
    private float sumAmpQ = 0;
    private int transitions = 0;
    private long current8Bytes = 0;

    private float localOscillatorI(float t) {
        return (float) Math.sin(2 * Math.PI * this.carrierFrequency * t);
    }

    private float localOscillatorQ(float t) {
        return (float) Math.cos(2 * Math.PI * this.carrierFrequency * t);
    }

    private byte getVal(int levels, int bitsPerSymbol) {
        float avgI = this.sumAmpI / this.samplesPerSymbolPeriod;
        float avgQ = this.sumAmpQ / this.samplesPerSymbolPeriod;

        byte iVal = 0, qVal = 0;
        float div = this.carrierAmplitude / levels;
        for (byte i = 0; i < levels; i++) {
            if (avgI > i * div - 0.5f * div && avgI < i * div + 0.5f * div) iVal = i;
            if (avgQ > i * div - 0.5f * div && avgQ < i * div + 0.5f * div) qVal = i;
        }

        qVal <<= bitsPerSymbol / 2;
        return (byte) (qVal + iVal);
    }

    @Override
    protected void initialCalculate(float[] samples, float timeStep) {
        this.samples = samples;
    }

    @Override
    public void next(float noise) {
        float t = this.index * this.timeStep;
        float f = this.samples[this.index] + noise;
        int bitsPerSymbol = (int) (Math.log(this.order) / Math.log(2));
        int levels = (int) Math.sqrt(bitsPerSymbol);

        float aI = f * localOscillatorI(t);
        float aQ = f * localOscillatorQ(t);

        // todo assume filtering already happened
        // After filtering, left with 1/2 Q(t) and 1/2 I(t)
        aI *= 2;
        aQ *= 2;
        this.sumAmpI += aI;
        this.sumAmpQ += aQ;

        int frame = (int) (t / this.symbolPeriod);
        if (this.transitions != frame) {
            this.transitions++;
            byte val = getVal(levels, bitsPerSymbol);

            this.current8Bytes <<= bitsPerSymbol;
            this.current8Bytes |= val;

            // For simplicity assume that bitsPerSymbol is either a multiple or exact divisor of 8, up to 64.
            // todo enforce this in simulatorsettings
            if (bitsPerSymbol <= 8 && this.transitions * bitsPerSymbol == 8) {
                this.buffer.addData((byte) (this.current8Bytes));
            } else if (bitsPerSymbol > 8) {
                long bitMask = 0xFFL << bitsPerSymbol;
                for (int i = 0; i < bitsPerSymbol / 8; i++) {
                    this.buffer.addData((byte) ((this.current8Bytes & bitMask) >> bitsPerSymbol));
                    this.current8Bytes >>= 8;
                }
            }
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
    }

    @Override
    public void reset() {
        super.reset();
        this.transitions = 0;
        this.sumAmpI = 0;
        this.sumAmpQ = 0;
    }
}
