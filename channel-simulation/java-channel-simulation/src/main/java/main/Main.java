package main;

import channel.Channel;
import org.jfree.data.xy.XYDataItem;
import receiver.ASKDemodulator;
import receiver.Receiver;
import simulator.Simulator;
import transmitter.ASKModulator;
import transmitter.Modulator;
import transmitter.RandomStream;
import transmitter.Transmitter;

import java.util.ArrayList;

public class Main {

    public static void byteListToString(ArrayList<Byte> bytes) {
        for (byte b : bytes) {
            String str = Integer.toBinaryString(b);
            if (str.length() > 8) {
                System.out.print(str.substring(24) + " ");
            } else {
                for (int i = 8; i > str.length(); i--) System.out.print("0");
                System.out.print(str + " ");
            }
        }
        System.out.println();
    }

    /*
    * EXPLANATION OF THE FOLLOWING BIT OPERATIONS:
    *   XOR (^) will set a bit if the input bits are different (a bit error).
    *   Java only performs bitwise operations on ints, which is why the type of error is int.
    *   Java treats bytes as signed 8-bit integers, so when casting a byte to an int (32-bit integer) it will make the integer negative
    *   if the first bit of the byte is set (e.g. 0xF0 becomes 0xFFFFFFF0). If the int is negative, there will be 24 extra "bit errors" in
    *   that byte, since the while loop will continue until all the bits have been shifted out. ANDing the byte with 0x000000FF will set
    *   the first 24 bits to 0, and leave the final 8 untouched.
    */
    private static double getBitErrorRate(Modulator modulator, Receiver receiver) {
        int bitErrors = 0;
        int n = Math.min(receiver.getDemodulator().getReceivedBytes().size(), modulator.getSentBytes().size());
        for (int i = 0; i < n; i++) {
            byte sentByte = modulator.getSentBytes().get(i);
            byte receivedByte = receiver.getDemodulator().getReceivedBytes().get(i);

            int error = (sentByte ^ receivedByte) & 0x000000FF;
            while (error != 0) {
                if ((error & 0b00000001) == 1) bitErrors++;
                error >>>= 1;
            }
        }
        return (double) bitErrors / n;
    }

    // [0] = modulator output
    // [1] = channel output
    private static XYDataItem[][] simulate(double sampleFrequency, double samplePeriod, Modulator modulator, Channel channel, Receiver receiver) {

        XYDataItem[] modOut = new XYDataItem[(int) (sampleFrequency * samplePeriod)];
        XYDataItem[] channelOut = new XYDataItem[(int) (sampleFrequency * samplePeriod)];

        double time = 0;
        for (int i = 0; i < modOut.length; i++) {
            double f = modulator.output(time);
            modOut[i] = new XYDataItem(time, f);
            f = channel.output(f);
            channelOut[i] = new XYDataItem(time, f);
            receiver.receive(f, time);

            time += samplePeriod / modOut.length;
        }
        receiver.receive(0, time + samplePeriod / modOut.length);

        return new XYDataItem[][]{modOut, channelOut};
    }

    private static void ber(double sampleFrequency, double samplePeriod, Modulator modulator, Channel channel, Receiver receiver) {
        ArrayList<XYDataItem> data = new ArrayList<>();
        for (double noise = 0; noise < 24; noise+=0.1) {
            System.out.println(noise);
            simulate(sampleFrequency, samplePeriod, modulator, channel, receiver);
            double ber = getBitErrorRate(modulator, receiver);
            data.add(new XYDataItem(noise, ber));
        }

        Plotter.plot("Bit error rate", "../assets/ber.png", "noise (dB)", "Bit error rate", new XYDataItem(1600, 900), data);
    }

    public static void main(String[] args) {
        double noise = 100;
        double depth = .8, amplitude = 1, carrierF = 20, modulationF = 5;
        ASKModulator modulator = new ASKModulator(depth, amplitude, carrierF, modulationF, new RandomStream());
        Channel channel = new Channel(null, modulator.getRMS(), noise);
        Receiver receiver = new Receiver(new ASKDemodulator(depth, amplitude, carrierF, modulationF));

        Simulator simulator = new Simulator(new Transmitter(modulator), receiver, channel, 0, 10, 0.001, false);

        Thread t1 = new Thread(() -> {
            while (!receiver.getDemodulator().getDataOut().isClosed()) {
                try {
                    System.out.println(receiver.getDemodulator().getDataOut().pop());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t1.start();

        try {
            simulator.simulate();
            t1.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        byteListToString(modulator.getSentBytes());
        byteListToString(receiver.getDemodulator().getReceivedBytes());
        System.out.println("error rate: " + getBitErrorRate(modulator, receiver));
    }
}
