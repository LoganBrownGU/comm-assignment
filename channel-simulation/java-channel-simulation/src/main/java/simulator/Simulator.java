package simulator;

import channel.Channel;
import display.Display;
import org.jfree.data.xy.XYDataItem;
import receiver.Receiver;
import transmitter.Transmitter;
import util.Plotter;

import java.util.ArrayList;

public class Simulator {

    private final double startTime, endTime, timeStep;
    private Transmitter transmitter;
    private Receiver receiver;
    private Channel channel;
    private final boolean realtime;

    public void simulate() throws InterruptedException {
        ArrayList<Double> transmitterData = this.transmitter.calculate(this.startTime, this.endTime, this.timeStep);
        ArrayList<Double> channelData = this.channel.calculate(transmitterData);
        this.receiver.receive(channelData, this.startTime, this.endTime, this.timeStep);
        ArrayList<Byte> dataIn = this.transmitter.getModulator().getSentBytes();
        ArrayList<Byte> dataOut = this.receiver.getDemodulator().getReceivedBytes();

        this.receiver.getDemodulator().getDataOut().close();

        XYDataItem[][] data = new XYDataItem[2][transmitterData.size()];
        for (int i = 0; i < data[0].length; i++) data[0][i] = new XYDataItem(this.startTime + this.timeStep * i, (double) transmitterData.get(i));
        for (int i = 0; i < data[0].length; i++) data[1][i] = new XYDataItem(this.startTime + this.timeStep * i, (double) channelData.get(i));
        //Plotter.plot("transmitted vs received", "../assets/trans-recv.png", "time", "amplitude", new XYDataItem(1600, 900), data);

        Display display = new Display("Sim", this.startTime, this.endTime, this.timeStep, transmitterData, channelData);
        display.run();

        /*if (this.display == null) return;
        System.out.println("waiting for display to be closed...");
        synchronized (this.display) {
            while (!this.display.isFinished()) this.display.wait();
        }
        this.display.dispose();*/
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

    public Simulator(Transmitter transmitter, Receiver receiver, Channel channel, double startTime, double endTime, double timeStep, Display display) {
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.channel = channel;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeStep = timeStep;
        this.realtime = true;
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
