package simulator;

import channel.Channel;
import receiver.Receiver;
import transmitter.Transmitter;

public class Simulator {

    private Transmitter transmitter;
    private Receiver receiver;
    private Channel channel;

    public void simulate() {
        long deltaT = System.nanoTime();
    }

    public Simulator(Transmitter transmitter, Receiver receiver, Channel channel) {
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.channel = channel;
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
