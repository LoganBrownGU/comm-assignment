package transmitter;

import java.util.Random;

public class RandomStream implements Datastream {

    Random rd = new Random();
    @Override
    public byte nextByte() {
        return (byte) rd.nextInt();
    }
}
