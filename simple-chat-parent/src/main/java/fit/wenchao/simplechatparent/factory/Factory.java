package fit.wenchao.simplechatparent.factory;


import fit.wenchao.simplechatparent.proto.codec.IProtoCodecFactory;
import fit.wenchao.simplechatparent.proto.codec.ProtoCodecFactory;
import fit.wenchao.simplechatparent.serializer.SerializerFactory;

public class Factory {
    public static IProtoCodecFactory getProtoCodecFactory() {
        return new ProtoCodecFactory(getSerializerFactory());
    }


    public static SerializerFactory getSerializerFactory() {
        return new SerializerFactory();
    }

}
