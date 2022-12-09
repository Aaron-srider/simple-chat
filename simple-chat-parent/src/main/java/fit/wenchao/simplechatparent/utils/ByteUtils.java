package fit.wenchao.simplechatparent.utils;

public class ByteUtils {
    public static IUnsignedByte allocUnsignedByte(int value) {
        return new UnsignedByte(value);
    }

    public static byte[] ofBytes(int... args) {
        int length = args.length;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) args[i];
        }
        return bytes;
    }

}
