package fit.wenchao.simplechatparent.serializer;


import org.springframework.stereotype.Component;

import java.io.*;


@Component
public class JDKSerializer implements ISerializer {

    @Override
    public byte[] serialize(Object obj) throws NotSerializableException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        ) {
            objectOutputStream.writeObject(obj);
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof NotSerializableException) {
                throw (NotSerializableException) e;
            }
            return null;
        }
    }
    @Override
    public <T> T read(byte[] srcBytes, Class<T> aclass) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(srcBytes);
        try (
                ObjectInputStream objectInputStream = new ObjectInputStream(in);
        ) {
            return (T) objectInputStream.readObject();
        }
    }
}
