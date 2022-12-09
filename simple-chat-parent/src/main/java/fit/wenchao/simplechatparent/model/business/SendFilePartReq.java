package fit.wenchao.simplechatparent.model.business;

import fit.wenchao.simplechatparent.proto.IMessageData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendFilePartReq implements IMessageData {
    // used for task saving in server
    String uuid;
    long segLen;
    String filename;
    String path;
    String fromUser;
    String toUser;
}
