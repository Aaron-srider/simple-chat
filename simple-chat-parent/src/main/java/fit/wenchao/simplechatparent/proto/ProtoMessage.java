package fit.wenchao.simplechatparent.proto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtoMessage implements IProtoMessage, Serializable {

    IMessageData messageData;

    IBusinessType businessType;

    @Override
    public IMessageData getMessageData() {
        return messageData;
    }

    @Override
    public IBusinessType getBusinessType() {
        return businessType;
    }
}
