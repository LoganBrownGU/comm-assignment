package receiver;

import java.util.ArrayList;

public class ASKDemodulator extends Demodulator {
    private final double depth, amplitude, carrierF, modulationPeriod;

    @Override
    public void receive(ArrayList<Double> samples, double start, double end, double step) {

        double maxAmp = 0;          // max amplitude for this time frame
        byte bitMask = 0b00000001, currentByte = 0;
        int transitions = 0;        // number of time frame transitions

        for (double t = 0, i = 0; t < end; t += step, i++) {
            double f = samples.get((int) i);
            maxAmp = Math.max(maxAmp, f);
            int frame = (int) Math.floor(t / this.modulationPeriod);
            // if the number of bit transitions does not match the current frame, then move to the
            // next bit.
            if (transitions != frame) {
                currentByte = updateByte(currentByte, bitMask, maxAmp > this.amplitude * (1 - this.depth / 2));

                maxAmp = 0;
                bitMask <<= 1;
                transitions++;
                if (bitMask == (byte) 0b00000000) bitMask = 0b00000001;
            }
        }
    }

    public ASKDemodulator(double depth, double carrierAmp, double carrierF, double modulationF) {
        super(new DataOut());
        this.depth = depth;
        this.amplitude = carrierAmp;
        this.carrierF = carrierF;
        this.modulationPeriod = 1 / modulationF;
    }


}
