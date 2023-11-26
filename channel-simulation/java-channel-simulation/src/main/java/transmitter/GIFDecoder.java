package transmitter;

public class GIFDecoder implements DataStream {
    @Override
    public byte nextByte() {
        return 0b01010101;
    }
}
