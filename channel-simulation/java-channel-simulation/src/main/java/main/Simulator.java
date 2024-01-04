package main;

import demodulator.Demodulator;
import display.SimulatorSettings;
import display.SimulatorView;
import modulator.ASKModulator;
import modulator.Modulator;
import modulator.ModulatorFactory;
import modulator.QAMModulator;
import org.jfree.data.xy.XYDataItem;
import util.Filter;
import util.Pair;
import util.Plotter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Simulator {

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
        if (!dir.isDirectory()) throw new RuntimeException(path + " is not a directory");

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

    private static boolean simulate() throws InterruptedException {
        SimulatorSettings simulatorSettings = new SimulatorSettings();
        simulatorSettings.run();
        // Wait for simulator settings to finish.
        synchronized (simulatorSettings.lock) {
            while (!simulatorSettings.isFinished()) simulatorSettings.lock.wait();
        }

        String path = "assets/frames";
        int framesToPlay = getNumFrames(path);
        Modulator modulator = simulatorSettings.getModulator();
        if (modulator == null) return false;
        byte[] data = readImages(path, framesToPlay);
        Dimension imageSize = readImageSize(path);
        // Set timeStep so that there are 100 samples per bit frame.
        float timeStep = 0.01f / modulator.getModulationFrequency();
        int framerate = 25;

        System.out.println("modulating...");
        float[] samples = modulator.calculate(data, timeStep);
        System.out.println("demodulating...");
        Demodulator demodulator = ModulatorFactory.getDemodulator(modulator, timeStep);
        assert demodulator != null;
        demodulator.initialCalculate(samples);

        simulatorSettings.dispose();

        SimulatorView simulatorView = new SimulatorView(modulator, demodulator, imageSize, framerate);
        Thread simulatorViewThread = new Thread(simulatorView);
        simulatorViewThread.setName("Simulator-View-Thread");

        DemodulationController controller = new DemodulationController(demodulator, simulatorView, samples);
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

        return simulatorView.getChangeSettings();
    }

    private static byte[] generateData() {
        Random rd = new Random();
        int size = 1024;
        byte[] data = new byte[size];
        rd.nextBytes(data);
        return data;
    }

    private static float findBER(Modulator modulator, byte[] inputData) {
        float timeStep = 0.01f / modulator.getModulationFrequency();

        float[] samples = modulator.calculate(inputData, timeStep);
        Demodulator demodulator = ModulatorFactory.getDemodulator(modulator, timeStep);
        demodulator.initialCalculate(samples);
        for (int j = 0; j < samples.length; j++) demodulator.next(0);

        byte[] outputData = new byte[inputData.length];
        int size = demodulator.buffer.getSize();
        System.arraycopy(demodulator.buffer.getChunk(size), 0, outputData, 0, size);

        /*if (modulator instanceof QAMModulator) {
            System.out.println(Integer.toBinaryString(Byte.toUnsignedInt(inputData[100])) + " " + Integer.toBinaryString(Byte.toUnsignedInt(inputData[101])));
            System.out.println(Integer.toBinaryString(Byte.toUnsignedInt(outputData[100])) + " " + Integer.toBinaryString(Byte.toUnsignedInt(outputData[101])));
        }*/

        return SimulatorView.findBER(inputData, outputData);
    }

    private static void writeData(String name, String path, XYDataItem[] data, Pair<String, String> headings) {
        try {
            File f = new File(path);
            BufferedWriter bf = new BufferedWriter(new FileWriter(f));
            bf.write(name + "\n");
            bf.write(headings.first + "," + headings.second + "\n");

            for (XYDataItem di : data)
                bf.write(di.getX() + "," + di.getY() + "\n");

            bf.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void testBandwidth() {
        byte[] inputData = readImages("assets/frames", 1);

        int dataPoints = 50;
        float start = 0.05f;
        float end = 0.2f;
        XYDataItem[] qamData = new XYDataItem[dataPoints];
        XYDataItem[] askData = new XYDataItem[dataPoints];

        int i = 0;
        for (float pc = start; pc <= end; pc += (end - start) / dataPoints, i++) {
            System.out.print((i+1) + "/" + dataPoints);
            float f_c = 10_000;

            Filter outputFilter = new Filter((int) (f_c * (1f - pc * 0.5f)), (int) (f_c * (1f + pc * 0.5f)));
            Modulator modulator = new QAMModulator(f_c, f_c / 10, 100, outputFilter);
            float ber = findBER(modulator, inputData);
            qamData[i] = new XYDataItem(pc * 100, ber);

            modulator = new ASKModulator(f_c, f_c / 10, 100, outputFilter, 0.5f);
            ber = findBER(modulator, inputData);
            askData[i] = new XYDataItem(pc * 100, ber);
            System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
        }

        writeData("QAM", "assets/qam.csv", qamData, new Pair<>("Bandwidth (% of $f_c$)", "BER"));
        writeData("ASK", "assets/ask.csv", askData, new Pair<>("Bandwidth (% of $f_c$)", "BER"));
    }

    public static void main(String[] args) throws InterruptedException {
        //while (simulate());
        testBandwidth();
    }
}
