package fit.wenchao.simplechatparent.proto.codec;

import fit.wenchao.simplechatparent.proto.*;
import fit.wenchao.simplechatparent.serializer.ISerializer;
import fit.wenchao.simplechatparent.serializer.SerializerFactory;
import fit.wenchao.simplechatparent.utils.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.NotSerializableException;

@Component
public class ProtoCodecDelegate {

    @Autowired
    ISerializer serializer;

    SerializerFactory serializerFactory;
    //

    public ProtoCodecDelegate() {
    }


    public ProtoCodecDelegate(SerializerFactory serializerFactory) {
        this.serializerFactory = serializerFactory;
    }

    public ByteBuf encode(Object obj) throws NotSerializableException {
        final SerializerType serializerType = SerializerType.JDK;
        //final int serializerType = 0;
        // serialNum, 4 bytes, 0
        final byte[] serialNum = new byte[4];

        byte[] data = serializer.serialize(obj);
        int len = data.length;

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        int packLen = 0;

        // magic 4
        buffer.writeBytes(ProtoMagics.defaultMagic.toBytes());
        // version 4
        buffer.writeBytes(ProtoVersion.VERSION_1_0.toBytes());
        // serializer type 1
        buffer.writeBytes(SerializerType.JDK.toBytes());
        // serial number 4
        buffer.writeBytes(serialNum);
        // padding 3
        buffer.writeBytes(ByteUtils.ofBytes(0, 0, 0));
        // data len 4
        buffer.writeInt(len);
        //header 20 bytes total

        // write data
        buffer.writeBytes(data);
        return buffer;
    }


    public <T> T decode(ByteBuf byteBuf, Class<T> tClass) throws IOException, ClassNotFoundException {
        byte[] magicBytes = new byte[ProtoMagic.len()];
        byteBuf.getBytes(0, magicBytes);
        ProtoMagic protoMagic = new ProtoMagic(magicBytes);
        if (!protoMagic.equals(ProtoMagics.defaultMagic)) {
            throw new IllegalArgumentException("Magic not recognised");
        }

        byte[] versionBytes = new byte[ProtoVersion.len()];
        byteBuf.getBytes(ProtoMagic.len(), versionBytes);
        ProtoVersion protoVersion = new ProtoVersion(versionBytes);
        if (!protoVersion.equals(ProtoVersion.VERSION_1_0)) {
            throw new IllegalArgumentException("Version not supported");
        }

        byte[] serializerTypeBytes = new byte[SerializerType.len()];
        byteBuf.getBytes(ProtoMagic.len() + ProtoVersion.len(), serializerTypeBytes);
        SerializerType protoSerializerType = new SerializerType();
        protoSerializerType.fromBytes(serializerTypeBytes);
        if (!protoSerializerType.equals(SerializerTypes.TYPE_JDK)) {
            throw new IllegalArgumentException("Serializer type not supported");
        }

        int dataLen = byteBuf.getInt(ProtoMagic.len() + ProtoVersion.len() + SerializerType.len() + 4 + 3);

        byte[] data = new byte[dataLen];

        byteBuf.getBytes(ProtoMagic.len() + ProtoVersion.len() + SerializerType.len() + 4 + 3 + 4, data, 0, dataLen);

        return serializer.read(data, tClass);

    }
}
