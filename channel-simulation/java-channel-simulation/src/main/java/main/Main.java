package main;

import channel.Channel;
import org.jfree.data.xy.XYDataItem;
import receiver.ASKDemodulator;
import receiver.Receiver;
import transmitter.ASKModulator;
import transmitter.RandomStream;

public class Main {
    public static void main(String[] args) {
        int samplePeriod = 8;
        int sampleFrequency = 1000;

        double depth = .8, amplitude = 1, carrierF = 5, modulationF = 1;

        XYDataItem[] data = new XYDataItem[sampleFrequency * samplePeriod];
        ASKModulator modulator = new ASKModulator(depth, amplitude, carrierF, modulationF, new RandomStream());
        Channel channel = new Channel(modulator.getRMS(), 24);
        Receiver receiver = new Receiver(new ASKDemodulator(depth, amplitude, carrierF, modulationF));

        double time = 0;
        for (int i = 0; i < data.length; i++) {
            double f = modulator.output(time);
            f = channel.output(f);
            data[i] = new XYDataItem(time, f);
            receiver.receive(f, time);

            time += (double) samplePeriod / data.length;
        }

        Plotter.plot("test", "../assets/plot.png", "t", "a", new XYDataItem(1000, 1000), data);
    }
}
