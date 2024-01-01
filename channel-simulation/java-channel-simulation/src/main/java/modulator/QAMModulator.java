package modulator;

public class QAMModulator extends Modulator {

    private final float levels;

    @Override
    public float[] calculate(byte[] data, float timeStep) {
        float bitPeriod = 1f / this.modulationFrequency;
        float endTime = data.length * 8 * bitPeriod;
        
        float[] samples = new float[(int) (endTime / timeStep)];

        return samples;
    }

    @Override
    public float getRMS() {
        return 0;
    }

    public QAMModulator(float carrierFrequency, float modulationFrequency, float carrierAmplitude, float levels) {
        super(carrierFrequency, modulationFrequency, carrierAmplitude);
        this.levels = levels;
    }
}
