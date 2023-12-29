package main;

import display.SimulatorSettings;
import display.SimulatorView;
import modulator.Modulator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Simulator {

    private static SimulatorSettings simulatorSettings;
    private static SimulatorView simulatorView;

    private static class SimulationController implements Runnable {
        @Override
        public void run() {

        }
    }

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
        simulatorSettings = new SimulatorSettings();
        simulatorSettings.run();
        synchronized (simulatorSettings.lock) {
            while (!simulatorSettings.isFinished()) simulatorSettings.lock.wait();
        }
        simulatorSettings.dispose();

        boolean useECC = simulatorSettings.useECC();
        Modulator modulator = simulatorSettings.getModulator();
        String path = "../assets/frames";
        byte[] data = readImages(path, 49);
        Dimension imageSize = readImageSize(path);
        float[] amp = modulator.calculate(data, 0.001f);
    }
}
