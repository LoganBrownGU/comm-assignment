package util;

public class Maths {
    public static float log2(float n) {
        return (float) (Math.log(n) / Math.log(2));
    }

    public static float log2(int n) {
        return (float) (Math.log(n) / Math.log(2));
    }

    public static byte reverseByte(byte b) {
        byte reversed = 0;
        for (int i = 0; i < 8; i++) {
            reversed += (byte) ((byte) ((b & 0x80) != 0 ? 1 : 0) << i);
            b <<= 1;
        }

        return reversed;
    }
}
