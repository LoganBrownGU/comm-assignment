package transmitter;

public class GIFDecoder implements Datastream {
    @Override
    public byte nextByte() {
        return 0b01010101;
    }
}
