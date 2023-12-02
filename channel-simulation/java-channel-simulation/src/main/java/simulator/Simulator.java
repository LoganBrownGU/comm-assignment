package simulator;

import channel.Channel;
import display.Display;
import main.Plotter;
import org.jfree.data.xy.XYDataItem;
import receiver.Receiver;
import transmitter.Transmitter;

import java.util.ArrayList;

public class Simulator {

    private final double startTime, endTime, timeStep;
    private Transmitter transmitter;
    private Receiver receiver;
    private Channel channel;
    private final boolean realtime;
    private final Display display;

    public void simulate() throws InterruptedException {
        // convert timeStep (s) into nanoseconds
        long sleepTime = (long) (this.timeStep * 1_000_000_000);
        ArrayList<XYDataItem> transmitterOut = new ArrayList<>();
        ArrayList<XYDataItem> channelOut = new ArrayList<>();

        for (double t = this.startTime; t < this.endTime; t += this.timeStep) {
            long deltaT = System.nanoTime();

            double f = this.transmitter.send(t);
            byte byteIn = this.transmitter.getCurrentByte();
            transmitterOut.add(new XYDataItem(t, f));
            f = this.channel.output(f);
            channelOut.add(new XYDataItem(t - this.timeStep * 1000, f));
            this.receiver.receive(f, t);
            byte byteOut = this.receiver.getCurrentByte();

            if (!this.realtime) continue;

            deltaT = sleepTime - (System.nanoTime() - deltaT);
            try {
                Thread.sleep(deltaT / 1_000_000, (int) (sleepTime - deltaT) % 1_000_000);
            } catch (IllegalArgumentException e) {
                System.out.println("exceeded timestep");
            }

            if (this.display == null) continue;
            if (this.display.isFinished()) break;
            this.display.update(transmitterOut.get(transmitterOut.size() - 1).getY().doubleValue(), channelOut.get(channelOut.size() - 1).getY().doubleValue(), byteIn, byteOut, this.timeStep);
        }


        XYDataItem[] transmitterArr = new XYDataItem[transmitterOut.size()];
        for (int i = 0; i < transmitterArr.length; i++) transmitterArr[i] = transmitterOut.get(i);
        XYDataItem[] channelArr = new XYDataItem[channelOut.size()];
        for (int i = 0; i < channelArr.length; i++) channelArr[i] = channelOut.get(i);

        Plotter.plot("Transmitter", "../assets/transmitter.png", "a", "t", new XYDataItem(1600, 900), new XYDataItem[][] {transmitterArr, channelArr});

        this.receiver.getDemodulator().getDataOut().close();

        if (this.display == null) return;
        System.out.println("waiting for display to be closed...");
        synchronized (this.display) {
            while (!this.display.isFinished()) this.display.wait();
        }
        this.display.dispose();
    }

    public Simulator(Transmitter transmitter, Receiver receiver, Channel channel, double startTime, double endTime, double timeStep, boolean realtime) {
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.channel = channel;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeStep = timeStep;
        this.realtime = realtime;
        this.display = null;
    }

    public Simulator(Transmitter transmitter, Receiver receiver, Channel channel, double startTime, double endTime, double timeStep, Display display) {
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.channel = channel;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeStep = timeStep;
        this.realtime = true;
        this.display = display;
        this.display.run();
    }

    public void setTransmitter(Transmitter transmitter) {
        this.transmitter = transmitter;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public double getTimeStep() {
        return this.timeStep;
    }
}
