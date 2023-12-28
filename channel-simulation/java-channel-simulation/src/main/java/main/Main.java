package main;

import display.SimulatorSettings;
import display.SimulatorView;
import modulator.ASKModulator;
import modulator.Modulator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    private static byte[] readImages(String path, int nImages) {
        ArrayList<Byte> data = new ArrayList<>();

        try {
            for (int i = 0; i < nImages; i++) {
                DataBuffer dfb = ImageIO.read(new File(path + "/" + i + ".png")).getData().getDataBuffer();
                for (int j = 0; j < dfb.getSize(); j++) data.add((byte) dfb.getElem(j));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] dataOut = new byte[data.size()];
        for (int i = 0; i < dataOut.length; i++) dataOut[i] = data.get(i);
        return dataOut;
    }

    private static Dimension readImageSize(String path) {
        int width, height;

        try {
            BufferedImage bi = ImageIO.read(new File(path + "/0.png"));
            width = bi.getWidth();
            height = bi.getHeight();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new Dimension(width, height);
    }

    public static void main(String[] args) {
        ASKModulator askModulator = new ASKModulator(10, 5, 1, 0.8);
        byte[] data = readImages("../assets/frames", 1);
        Dimension imageSize = readImageSize("../assets/frames");
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

        SimulatorView simulatorView = new SimulatorView(modulator, null, 1000000, imageSize, 24);
        simulatorView.run();
        try {
            synchronized (simulatorView.lock) {
                while (!simulatorView.isFinished()) simulatorView.lock.wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        simulatorView.dispose();

        /*XYDataItem[] dataItems = new XYDataItem[amp.length];
        for (double t = 0, i = 0; i < dataItems.length; t += 0.001, i++) dataItems[(int) i] = new XYDataItem(t, amp[(int) i]);
        Plotter.plot("test", "assets/test.png", "t", "a", new XYDataItem(1600, 900), dataItems);*/
    }
}