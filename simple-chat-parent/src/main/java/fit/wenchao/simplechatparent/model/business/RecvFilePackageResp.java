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
public class RecvFilePackageResp implements IMessageData {
    String code;
    String uuid;
    String actualFileName;
    long writeLen;
    String from;
    String to;
    long serialNum;
}
