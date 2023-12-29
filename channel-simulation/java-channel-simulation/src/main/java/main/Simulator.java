package main;

import demodulator.ASKDemodulator;
import demodulator.Demodulator;
import display.SimulatorSettings;
import display.SimulatorView;
import modulator.ASKModulator;
import modulator.Modulator;
import modulator.ModulatorFactory;
import org.jfree.data.xy.XYDataItem;
import util.Plotter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Simulator {

    private static SimulatorSettings simulatorSettings;
    private static SimulatorView simulatorView;

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

    public static void simulate() throws InterruptedException {
        /*simulatorSettings = new SimulatorSettings();
        simulatorSettings.run();
        synchronized (simulatorSettings.lock) {
            while (!simulatorSettings.isFinished()) simulatorSettings.lock.wait();
        }

        boolean useECC = simulatorSettings.useECC();
        float snr = simulatorSettings.getSNR();
        float timeStep = 0.001f;
        Modulator modulator = simulatorSettings.getModulator();
        String path = "../assets/frames";
        byte[] data = readImages(path, 10);
        Dimension imageSize = readImageSize(path);
        Demodulator demodulator = ModulatorFactory.getDemodulator(modulator);*/

        ASKModulator modulator = new ASKModulator(10000, 5000, 1, .8f);
        String path = "../assets/frames";
        byte[] data = readImages(path, 10);
        Dimension imageSize = readImageSize(path);
        float timeStep = 0.000001f;
        float snr = 12;

        System.out.println("modulating...");
        float[] amp = modulator.calculate(data, timeStep);
        XYDataItem[] dataItems = new XYDataItem[10000];
        for (float t = 0, i = 0; i < dataItems.length; t += timeStep, i++) dataItems[(int) i] = new XYDataItem(t, amp[(int) i]);
        Plotter.plot("test", "assets/test.png", "t", "a", new XYDataItem(1600, 900), dataItems);
        System.out.println("demodulating...");
        Demodulator demodulator = ModulatorFactory.getDemodulator(modulator, amp, timeStep);

        //simulatorSettings.dispose();

        SimulationController controller = new SimulationController(timeStep, amp, demodulator);

        simulatorView = new SimulatorView(controller, modulator, demodulator, imageSize, 25);
        simulatorView.run();

        synchronized (simulatorView.lock) {
            while (!simulatorView.isFinished()) simulatorView.lock.wait();
        }
        simulatorView.dispose();
    }
}
