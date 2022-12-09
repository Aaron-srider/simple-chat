package fit.wenchao.simplechatparent.proto;

import java.io.Serializable;

@Deprecated
public class MessageType implements IMessageType , Serializable {
    private String type;

    public MessageType(String type) {
        this.type = type;
    }
}
