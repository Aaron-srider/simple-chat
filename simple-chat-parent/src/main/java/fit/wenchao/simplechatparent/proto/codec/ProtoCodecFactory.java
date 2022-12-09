package fit.wenchao.simplechatparent.proto.codec;


import fit.wenchao.simplechatparent.serializer.SerializerFactory;

public class ProtoCodecFactory implements IProtoCodecFactory {

    SerializerFactory serializerFactory;

    public ProtoCodecFactory(SerializerFactory serializerFactory) {
        this.serializerFactory = serializerFactory;
    }

    @Override
    public ProtoCodec getProtoCodec() {
        ProtoCodecDelegate protoCodecDelegate = new ProtoCodecDelegate(serializerFactory);
        return new ProtoCodec(protoCodecDelegate);
    }
}
