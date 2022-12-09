package fit.wenchao.simplechatparent.proto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 8 bytes
 */
public class ProtoMagic {
     private byte[] value;

    private static final int bytesLen = 4;

    public int count () {
        return bytesLen;
    }

    public static int len () {
        return bytesLen;
    }


    public ProtoMagic(byte[] value) {
        copy(value);
    }

    public ProtoMagic() {

    }



    public ProtoMagic(String src) {
        byte[] bytes = src.getBytes(StandardCharsets.UTF_8);
        copy(bytes);
    }


    private void copy(byte[] value) {
        this.value = new byte[bytesLen];
        int count;

        if (value.length <= bytesLen) {
            count = value.length;
        } else {
            count = bytesLen;
        }
        System.arraycopy(value, 0, this.value, 0, count);
    }

    public byte[] toBytes() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProtoMagic that = (ProtoMagic) o;
        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}