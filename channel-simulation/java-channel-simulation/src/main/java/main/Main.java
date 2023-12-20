package main;

import channel.Channel;
import display.Display;
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
        return (double) bitErrors / (n * 8);
    }

    public static void main(String[] args) {
        double noise = 24;
        double depth = .8, amplitude = 1, carrierF = 15, modulationF = 7;
        ASKModulator modulator = new ASKModulator(depth, amplitude, carrierF, modulationF, new RandomStream());
        Channel channel = new Channel(new Filter(0, 80), modulator.getRMS(), noise);
        Receiver receiver = new Receiver(new ASKDemodulator(depth, amplitude, carrierF, modulationF));

        Simulator simulator = new Simulator(new Transmitter(modulator, null), receiver, channel, 0, 5, 0.00001, false);

        Thread t1 = new Thread(() -> {
            while (!receiver.getDemodulator().getDataOut().isClosed()) {
                try {
                    System.out.println(receiver.getDemodulator().getDataOut().pop());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        try {
            simulator.simulate();
            //(t1.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

//        byteListToString(modulator.getSentBytes());
//        byteListToString(receiver.getDemodulator().getReceivedBytes());
//        System.out.println("error rate: " + getBitErrorRate(modulator, receiver));
    }
}
