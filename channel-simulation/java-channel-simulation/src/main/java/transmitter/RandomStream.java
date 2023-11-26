package transmitter;

import java.util.Random;

public class RandomStream implements DataStream {

    private final Random rd = new Random();

    @Override
    public byte nextByte() {
        byte b = (byte) rd.nextInt();
        return b;
    }
}
