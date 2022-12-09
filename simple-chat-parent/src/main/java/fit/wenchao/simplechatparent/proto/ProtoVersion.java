package fit.wenchao.simplechatparent.proto;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 4 bytes
 */
public class ProtoVersion {
    final private String value;

    public ProtoVersion(String value) {
        this.value = value;
    }

    public ProtoVersion(byte[] value) {
        if(value.length != len()) {
            throw new IllegalArgumentException("ProtoVersion len does not equal to " + len());
        }
        this.value = new String(value, StandardCharsets.UTF_8);
    }

    public static final ProtoVersion VERSION_1_0 = new ProtoVersion(" 1.0");

    @Override
    public String toString() {
        return value.trim();
    }
    
    public byte[] toBytes() {
        return value.getBytes(StandardCharsets.UTF_8);
    }



    public int count () {
        return 4;
    }

    public static int len () {
        return 4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProtoVersion that = (ProtoVersion) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}