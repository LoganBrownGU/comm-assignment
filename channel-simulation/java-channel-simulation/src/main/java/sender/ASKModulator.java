package sender;

public class ASKModulator implements Modulator {

    private double depth, amplitude, carrierF, modulationF;
    private Datastream decoder;

    @Override
    public double output(double t) {
        return 0;
    }

    public ASKModulator(double depth, double amplitude, double carrierF, double modulationF) {
        this.depth = depth;
        this.amplitude = amplitude;
        this.carrierF = carrierF;
        this.modulationF = modulationF;
    }
}
