package demodulator;

import java.util.Random;

public class ASKDemodulator extends Demodulator {

    private final float depth, amplitude, modulationPeriod;

    @Override
    public void calculate(float[] amp, float snr, float timeStep) {
        float signalRMS = this.amplitude / (float) Math.sqrt(2);
        float noiseRMS = (float) (signalRMS / Math.pow(10, snr/20));
        Random rd = new Random();

        float maxAmp = 0;
        int transitions = 0;
        byte bitMask = 0x01;
        byte currentByte = 0;

        int i = 0;
        for (float t = 0; i < amp.length; t += timeStep, i++) {
            float f = amp[i] + (float) rd.nextGaussian() * noiseRMS;
            maxAmp = Math.max(f, maxAmp);
            int bitFrame = (int) (t / this.modulationPeriod);

            if (bitFrame != transitions) {
                boolean bit = maxAmp > this.amplitude * (1 - this.depth / 2);
                currentByte = updateByte(currentByte, bitMask, bit);

                bitMask <<= 1;
                transitions++;
                maxAmp = 0;
                if (bitMask == 0x00) {
                    bitMask = 0x01;
                    this.buffer.addData(currentByte);
                }
            }
        }
    }

    public ASKDemodulator(float depth, float amplitude, float modulationFrequency) {
        this.depth = depth;
        this.amplitude = amplitude;
        this.modulationPeriod = 1f / modulationFrequency ;
    }
}
