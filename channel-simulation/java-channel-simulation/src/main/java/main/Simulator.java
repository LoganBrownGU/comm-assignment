package main;

import demodulator.Demodulator;
import display.SimulatorSettings;
import display.SimulatorView;
import modulator.ASKModulator;
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
import java.util.Objects;

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

    private static int getNumFrames(String path) {
        File dir = new File(path);
        if (!dir.isDirectory()) throw new RuntimeException("path must be directory");

        int n = 0;
        for (File f : Objects.requireNonNull(dir.listFiles()))
            if (f.toString().endsWith(".png")) n++;

        return n;
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

        String path = "../assets/frames";
        int framesToPlay = getNumFrames(path);
        ASKModulator modulator = new ASKModulator(78000, 39000, 1, .8f);
        byte[] data = readImages(path, framesToPlay);
        Dimension imageSize = readImageSize(path);
        float timeStep = 0.000001f;
        int framerate = 25;
        float snr = 12;

        System.out.println("modulating...");
        float[] amp = modulator.calculate(data, timeStep);
        XYDataItem[] dataItems = new XYDataItem[10000];
        for (float t = 0, i = 0; i < dataItems.length; t += timeStep, i++) dataItems[(int) i] = new XYDataItem(t, amp[(int) i]);
        Plotter.plot("test", "assets/test.png", "t", "a", new XYDataItem(1600, 900), dataItems);
        System.out.println("demodulating...");
        Demodulator demodulator = ModulatorFactory.getDemodulator(modulator, amp, timeStep);

        //simulatorSettings.dispose();

        simulatorView = new SimulatorView(modulator, demodulator, imageSize, framerate, framesToPlay);
        Thread simulatorViewThread = new Thread(simulatorView);
        simulatorViewThread.setName("Simulator-View-Thread");

        DemodulationController controller = new DemodulationController(demodulator, simulatorView, framerate, framesToPlay, amp);
        Thread controllerThread = new Thread(controller);
        controllerThread.setName("Controller-Thread");

        controllerThread.start();
        simulatorViewThread.start();

        synchronized (simulatorView.finishedLock) {
            while (!simulatorView.isFinished()) simulatorView.finishedLock.wait();
        }
        controllerThread.interrupt();
        controllerThread.join();
        simulatorView.dispose();
        simulatorViewThread.join();

        /*XYDataItem[] xydata = ((ASKDemodulator) demodulator).data;
        Plotter.plot("demodulator", "assets/demod.png", "t", "a", new XYDataItem(1600, 900), xydata);*/
    }
}
