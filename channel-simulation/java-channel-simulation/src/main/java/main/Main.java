package main;

import display.SimulatorSettings;
import modulator.ASKModulator;
import modulator.Modulator;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ASKModulator askModulator = new ASKModulator(10, 5, 1, 0.8);
        byte[] data = {'s', 'a'};
        double[] amp = askModulator.calculate(data, 0.001);

        SimulatorSettings simSettings = new SimulatorSettings();
        simSettings.run();
        try {
            synchronized (simSettings.lock) {
                while (!simSettings.isFinished()) simSettings.lock.wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        simSettings.dispose();

        boolean useECC = simSettings.useECC();
        ArrayList<String> params = simSettings.getModulatorParameters();
        System.out.println("use ECC: " + useECC);
        for (String s : params) System.out.println(s);

        Modulator modulator = simSettings.getModulator();


        /*XYDataItem[] dataItems = new XYDataItem[amp.length];
        for (double t = 0, i = 0; i < dataItems.length; t += 0.001, i++) dataItems[(int) i] = new XYDataItem(t, amp[(int) i]);
        Plotter.plot("test", "assets/test.png", "t", "a", new XYDataItem(1600, 900), dataItems);*/
    }
}