package main;

import channel.Channel;
import org.jfree.data.xy.XYDataItem;
import receiver.ASKDemodulator;
import receiver.Receiver;
import transmitter.ASKModulator;
import transmitter.RandomStream;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    /*
    * EXPLANATION OF THE FOLLOWING BIT OPERATIONS:
    *   XOR (^) will set a bit if the input bits are different (a bit error).
    *   Java only performs bitwise operations on ints, which is why the type of error is int.
    *   Java treats bytes as signed 8-bit integers, so when casting a byte to an int (32-bit integer) it will make the integer negative
    *   if the first bit of the byte is set (e.g. 0xF0 becomes 0xFFFFFFF0). If the int is negative, there will be 24 extra "bit errors" in
    *   that byte, since the while loop will continue until all the bits have been shifted out. ANDing the byte with 0x000000FF will set
    *   the first 24 bits to 0, and leave the final 8 untouched.
    */
    private static int getBitErrors(ASKModulator modulator, Receiver receiver) {
        int bitErrors = 0;
        for (int i = 0; i < receiver.getDemodulator().getReceivedBytes().size(); i++) {
            byte sentByte = modulator.getSentBytes().get(i);
            byte receivedByte = receiver.getDemodulator().getReceivedBytes().get(i);

            int error = (sentByte ^ receivedByte) & 0x000000FF;
            while (error != 0) {
                if ((error & 0b00000001) == 1) bitErrors++;
                error >>>= 1;
            }
        }
        return bitErrors;
    }

    private static double simulate(double noise, int bytesToSend) {
        int samplePeriod = 8 * bytesToSend;
        int sampleFrequency = 100;

        double depth = .8, amplitude = 1, carrierF = 5, modulationF = 1;

        XYDataItem[] data = new XYDataItem[sampleFrequency * samplePeriod];
        ASKModulator modulator = new ASKModulator(depth, amplitude, carrierF, modulationF, new RandomStream());
        Channel channel = new Channel(new Filter(2, 7), modulator.getRMS(), noise);
        Receiver receiver = new Receiver(new ASKDemodulator(depth, amplitude, carrierF, modulationF));

        double time = 0;
        for (int i = 0; i < data.length; i++) {
            double f = modulator.output(time);
            f = channel.output(f);
            data[i] = new XYDataItem(time, f);
            receiver.receive(f, time);

            time += (double) samplePeriod / data.length;
        }
        Plotter.plot("Channel", "../assets/channel.png", "t", "a", new XYDataItem(1600, 900), data);
        receiver.receive(0, time + (double) samplePeriod / data.length);

        int bitErrors = getBitErrors(modulator, receiver);
        return (double) bitErrors / (receiver.getDemodulator().getReceivedBytes().size() * 8);
    }

    private void ber() {
        ArrayList<XYDataItem> data = new ArrayList<>();
        for (double noise = 0; noise < 24; noise+=0.1) {
            double ber = simulate(noise, 1024);
            data.add(new XYDataItem(noise, ber));
        }

        Plotter.plot("Bit error rate", "../assets/ber.png", "noise (dB)", "Bit error rate", new XYDataItem(1600, 900), data);
    }

    public static void main(String[] args) {
        Filter filter = new Filter(3, 6);
        ArrayList<XYDataItem> data = new ArrayList<>();

        for (double t = 0; t < 10; t += 0.001) {
            double f = Math.sin(2 * Math.PI * 5 * t);
            f = filter.output(f);
            data.add(new XYDataItem(t, f));
        }

        Plotter.plot("Channel", "../assets/channel.png", "t", "a", new XYDataItem(1600, 900), data);
    }
}
