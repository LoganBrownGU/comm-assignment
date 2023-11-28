package simulator;

import channel.Channel;
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

    public void simulate() throws InterruptedException {
        // convert timeStep (s) into nanoseconds
        long sleepTime = (long) (timeStep * 1_000_000_000);
        ArrayList<XYDataItem> transmitterOut = new ArrayList<>();
        ArrayList<XYDataItem> channelOut = new ArrayList<>();

        for (double t = startTime; t < endTime; t += timeStep) {
            long deltaT = System.nanoTime();

            double f = transmitter.send(t);
            transmitterOut.add(new XYDataItem(t, f));
            f = channel.output(f);
            channelOut.add(new XYDataItem(t, f));
            receiver.receive(f, t);

            if (!realtime) continue;

            deltaT = sleepTime - (System.nanoTime() - deltaT);
            if (deltaT < 0) System.out.println(deltaT + " " + (System.nanoTime() - deltaT));
            Thread.sleep(deltaT / 1_000_000, (int) (sleepTime - deltaT) % 1_000_000);
        }

        Plotter.plot("Transmitter", "../assets/transmitter.png", "a", "t", new XYDataItem(1600, 900), transmitterOut);
        Plotter.plot("Channel", "../assets/channel.png", "a", "t", new XYDataItem(1600, 900), channelOut);

        receiver.getDemodulator().getDataOut().close();
    }

    public Simulator(Transmitter transmitter, Receiver receiver, Channel channel, double startTime, double endTime, double timeStep, boolean realtime) {
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.channel = channel;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeStep = timeStep;
        this.realtime = realtime;
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
}
