package fit.wenchao.simplechatparent.utils;

import java.util.Iterator;

/**
 * max 32 bits
 */
public class BitArray {
    private int value;

    private int cap = 32;

    private int len = 0;

    public int get(int idx) {
        return (value >> idx) & 0x1;
    }

    public int size() {
        return len;
    }

    public Iterator<Byte> getIterator() {
        BitArray athis = this;
        return new Iterator<Byte>() {
            int pos = 32 - athis.len;
            final BitArray bitArray = athis;
            @Override
            public boolean hasNext() {
                return pos < 32;
            }

            @Override
            public Byte next() {
                byte result = (byte) bitArray.get(32 - pos - 1);
                pos ++ ;
                return result;
            }
        };
    }


    public static BitArray convert(long integer, int lowCount) {
        int len = lowCount;
        if (len > 32) {
            throw new IllegalArgumentException("BitArray supports only 32 bits max");
        }

        BitArray bitArray = new BitArray();

        for (int i = 0; i < len; i++) {
            bitArray.addBit((byte) (integer & 0x1));
            integer = integer >> 1;
        }

        return bitArray;
    }

    public void setBit(byte zeroOrOne, int idx) {
        if (zeroOrOne == 1) {
            value = (0x1 << idx) | value;
        } else if (zeroOrOne == 0) {
            value = value & (~(0x1 << idx));
        } else {
            throw new IllegalArgumentException("Bit can only be one or zero");
        }
    }

    public void addBit(byte zeroOrOne) {
        if (len >= cap) {
            throw new IllegalArgumentException("BitArray supports only 32 bits max");
        }

        setBit(zeroOrOne, len++);
    }


}
