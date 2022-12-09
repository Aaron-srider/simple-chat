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
public class ReceiveMsgResp implements IMessageData {
    private String text;
    private String from;
    private String to;
}
