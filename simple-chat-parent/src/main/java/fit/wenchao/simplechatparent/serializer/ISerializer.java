package fit.wenchao.simplechatparent.serializer;

import java.io.IOException;
import java.io.NotSerializableException;

public interface ISerializer {
    byte[] serialize(Object obj) throws NotSerializableException;

    <T> T read(byte[] srcBytes, Class<T> aclass) throws IOException, ClassNotFoundException;
}
