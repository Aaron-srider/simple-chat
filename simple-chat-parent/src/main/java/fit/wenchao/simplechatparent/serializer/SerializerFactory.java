package fit.wenchao.simplechatparent.serializer;

public class SerializerFactory {
    public ISerializer getSerializer() {
        return new JDKSerializer();
    }

    public ISerializer getSerializer(String name) {
        return null;
    }
}
