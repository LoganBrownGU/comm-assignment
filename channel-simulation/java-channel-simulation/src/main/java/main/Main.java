package main;

import org.jfree.data.xy.XYDataItem;
import transmitter.ASKModulator;
import transmitter.GIFDecoder;
import transmitter.RandomStream;

public class Main {
    public static void main(String[] args) {
        int samplePeriod = 8;
        int sampleFrequency = 1000;

        XYDataItem[] data = new XYDataItem[sampleFrequency * samplePeriod];
        ASKModulator modulator = new ASKModulator(0.8, 1, 5, 1, new RandomStream());

        double time = 0;
        for (int i = 0; i < data.length; i++) {
            data[i] = new XYDataItem(time, modulator.output(time));
            time += (double) samplePeriod / data.length;
        }

        Plotter.plot("test", "../assets/plot.png", "t", "a", new XYDataItem(1000, 1000), data);
    }
}
