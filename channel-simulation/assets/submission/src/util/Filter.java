package util;

import org.jtransforms.fft.FloatFFT_1D;

public class Filter {
    private final int highCutoff, lowCutoff;

    private void removeFrequencies(float[] components) {
        for (int i = 0; i <= this.lowCutoff; i++) {
            components[i*2] = 0;
            components[i*2+1] = 0;
        }

        for (int i = this.highCutoff; i < components.length / 2; i++) {
            components[i*2] = 0;
            components[i*2+1] = 0;
        }
    }

    public void filter(float[] samples, float timeStep) {

        // To reduce memory usage and avoid scaling, do the Fourier Transform 1 second at a time.
        int samplesPerSecond = (int) Math.round(1d / timeStep);
        for (int i = 0; (i + 1) * samplesPerSecond < samples.length; i++) {
            float[] newSamples = new float[samplesPerSecond * 2];
            System.arraycopy(samples, i * samplesPerSecond, newSamples, 0, samplesPerSecond);
            FloatFFT_1D fft = new FloatFFT_1D(samplesPerSecond);
            fft.realForward(newSamples);
            removeFrequencies(newSamples);
            fft.complexInverse(newSamples, false);

            for (int j = 0; j < newSamples.length / 2; j++)
                samples[j + i * samplesPerSecond] = newSamples[j * 2] / ((float) samplesPerSecond / 2);
        }
    }

    public Filter(int lowCutoff, int highCutoff) {
        if (lowCutoff > highCutoff) throw new IllegalArgumentException("high cutoff must be greater than low cutoff");
        if (lowCutoff < 0) throw new IllegalArgumentException("cutoffs must be greater than or equal to 0");

        this.highCutoff = highCutoff;
        this.lowCutoff = lowCutoff;
    }
}
