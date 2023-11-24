package transmitter;

import java.util.Random;

public class RandomStream implements Datastream {

    Random rd = new Random();
    @Override
    public byte nextByte() {
        byte b = (byte) rd.nextInt();
        System.out.print(b + " ");
        return b;
    }
}
