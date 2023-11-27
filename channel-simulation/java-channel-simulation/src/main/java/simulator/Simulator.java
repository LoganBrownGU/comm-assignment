package simulator;

import channel.Channel;
import receiver.Receiver;
import transmitter.Transmitter;

public class Simulator {

    private final double startTime, endTime, timeStep;

    private Transmitter transmitter;
    private Receiver receiver;
    private Channel channel;

    public void simulate() throws InterruptedException {
        // convert timeStep (s) into nanoseconds
        long sleepTime = (long) (timeStep * 1_000_000_000);
        System.out.println(sleepTime);
        for (double t = startTime; t < endTime; t += timeStep) {
            long deltaT = System.nanoTime();

            double f = transmitter.send(t);
            f = channel.output(f);
            receiver.receive(f, t);

            deltaT = sleepTime - (System.nanoTime() - deltaT);
            if (deltaT < 0) System.out.println(deltaT + " " + (System.nanoTime() - deltaT));
            Thread.sleep(deltaT / 1_000_000, (int) (sleepTime - deltaT) % 1_000_000);
        }

        //receiver.getDemodulator().getDataOut().close();
    }

    public Simulator(Transmitter transmitter, Receiver receiver, Channel channel, double startTime, double endTime, double timeStep) {
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.channel = channel;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeStep = timeStep;
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
