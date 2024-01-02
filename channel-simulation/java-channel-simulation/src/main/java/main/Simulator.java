package main;

import demodulator.Demodulator;
import display.SimulatorSettings;
import display.SimulatorView;
import modulator.ASKModulator;
import modulator.Modulator;
import modulator.ModulatorFactory;
import org.jfree.data.xy.XYDataItem;
import util.Filter;
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

    // Read in frames as raw byte data.
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

    // Count number of frames.
    private static int getNumFrames(String path) {
        File dir = new File(path);
        if (!dir.isDirectory()) throw new RuntimeException("path must be directory");

        int n = 0;
        for (File f : Objects.requireNonNull(dir.listFiles()))
            if (f.toString().endsWith(".png")) n++;

        return n;
    }

    // Read the dimensions of the frames.
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

    public static void main(String[] args) throws InterruptedException {

        /*float periods = 4.4f;
        float[] f = new float[(int) (periods * 100000)];
        float step = periods / f.length;
        System.out.println(f.length * step);
        for (int i = 0; i < f.length; i++) {
            float t = step * i;
            f[i] = (float) (Math.sin(2 * Math.PI * 10 * t) * Math.sin(2 * Math.PI * 3 * t));
        }
        XYDataItem[] xydata = new XYDataItem[f.length];
        for (int i = 0; i < xydata.length; i++) {
            xydata[i] = new XYDataItem(i * step, f[i]);
        }
        Plotter.plot("pre filter", "assets/prefilter.png", "t", "a", new XYDataItem(1600, 900), xydata);
        new Filter(9, 11).filter(f, step);
        xydata = new XYDataItem[f.length];
        for (int i = 0; i < xydata.length; i++) {
            xydata[i] = new XYDataItem(i * step, f[i]);
        }
        Plotter.plot("post filter", "assets/postfilter.png", "t", "a", new XYDataItem(1600, 900), xydata);

        System.exit(0);*/

        simulatorSettings = new SimulatorSettings();
        simulatorSettings.run();
        // Wait for simulator settings to finish.
        synchronized (simulatorSettings.lock) {
            while (!simulatorSettings.isFinished()) simulatorSettings.lock.wait();
        }

        String path = "../assets/frames";
        // todo reset this
        int framesToPlay = getNumFrames(path);
        Modulator modulator = simulatorSettings.getModulator();
        byte[] data = readImages(path, framesToPlay);
        Dimension imageSize = readImageSize(path);
        // Set timeStep so that there are 100 samples per bit frame.
        float timeStep = 0.01f / modulator.getModulationFrequency();
        System.out.println(timeStep);
        int framerate = 25;

        System.out.println("modulating...");
        float[] amp = modulator.calculate(data, timeStep);
        System.out.println("demodulating...");
        Demodulator demodulator = ModulatorFactory.getDemodulator(modulator, amp, timeStep);

        simulatorSettings.dispose();

        simulatorView = new SimulatorView(modulator, demodulator, imageSize, framerate);
        Thread simulatorViewThread = new Thread(simulatorView);
        simulatorViewThread.setName("Simulator-View-Thread");

        DemodulationController controller = new DemodulationController(demodulator, simulatorView, amp);
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
    }
}
