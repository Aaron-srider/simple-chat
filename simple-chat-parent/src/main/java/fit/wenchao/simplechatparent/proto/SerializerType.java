package fit.wenchao.simplechatparent.proto;


import fit.wenchao.simplechatparent.utils.UnsignedByte;

import java.util.Objects;

public class SerializerType {


     private UnsignedByte value;

    public SerializerType(int value) {
        this.value = new UnsignedByte(value);
    }

    public SerializerType() {

    }


    public void fromBytes(byte[] bytes)
    {
        if(bytes.length != 1){
            throw new IllegalArgumentException("len not equals to: "+count());
        }
        byte value = bytes[0];
        this.value = new UnsignedByte(value);
    }



    public static final SerializerType JDK = new SerializerType(0);

    public byte[] toBytes() {
        return new byte[]{this.value.toByte()};
    }


    public int count () {
        return 1;
    }

    public static int len () {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerializerType that = (SerializerType) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}