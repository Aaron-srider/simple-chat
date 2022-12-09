package fit.wenchao.simplechatparent.utils;

import java.util.Objects;

public class UnsignedByte implements IUnsignedByte {
    private byte value;

    public UnsignedByte(int value) {
       this.set(value);
    }

    public UnsignedByte(byte value) {
        this.fromByte(value);
    }

    public void fromByte(byte oneByte) {
        this.value = oneByte;
    }

    public UnsignedByte() {

    }

    @Override
    public int getReadableValue() {
        return 0xff & value;
    }

    @Override
    public byte toByte() {
        return value;
    }

    @Override
    public void set(int value) {
        if (inRange(value)) {
            this.value = (byte) value;
            return;
        }
        throw new IllegalArgumentException("Arg value must between 0 and 255");
    }

    private boolean inRange(int value) {
        return 0 <= value && value <= 255;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnsignedByte that = (UnsignedByte) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}