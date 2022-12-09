package fit.wenchao.simplechatparent.model.business;


import fit.wenchao.simplechatparent.proto.IMessageData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMsgResp implements IMessageData {
    private String code;
}
