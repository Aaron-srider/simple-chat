package fit.wenchao.simplechatparent.utils;

public class BitUtils {
    public static int getBit(int startIdx,int count, int integer) {
        int intLen = 32;
        int moveRight = intLen - (count+1+startIdx);
        return (integer>>moveRight)&0x1;
    }
}
