package fit.wenchao.simplechatparent.proto.codec;

import fit.wenchao.simplechatparent.proto.IProtoMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChannelHandler.Sharable
public class ProtoCodec extends MessageToMessageCodec<ByteBuf, IProtoMessage> {

    @Autowired
    ProtoCodecDelegate protoCodecDelegate;

    public ProtoCodec() {
    }

    public ProtoCodec(ProtoCodecDelegate protoCodecDelegate) {
        this.protoCodecDelegate = protoCodecDelegate;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, IProtoMessage msg, List<Object> out) throws Exception {
        ByteBuf buffer = protoCodecDelegate.encode(msg);
        out.add(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        IProtoMessage obj = protoCodecDelegate.decode(in, IProtoMessage.class);
        out.add(obj);
    }
}
