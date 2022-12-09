package fit.wenchao.simplechatparent.proto.codec;

import fit.wenchao.simplechatparent.constants.FileSizes;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class FrameDecoder extends LengthFieldBasedFrameDecoder {

    public FrameDecoder() {
        this((int) (FileSizes.ONE_M * 10), 16, 4);
    }

    private FrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }
}
