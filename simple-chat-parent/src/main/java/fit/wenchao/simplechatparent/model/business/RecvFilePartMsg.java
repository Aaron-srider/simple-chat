package fit.wenchao.simplechatparent.model.business;

import fit.wenchao.simplechatparent.proto.IMessageData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import static fit.wenchao.simplechatparent.utils.StrUtils.ft;

@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecvFilePartMsg implements IMessageData {
    String uuid;
    String filename;
    byte [] bytes;
    String toUser;
    String fromUser;
    long spos;
    long len;
    long serialNum;
    boolean over;

    public String reportFilePart(){
        return ft("Pack No.: {}, Pack Route: {} -> {}, Pack Len: {}", serialNum, fromUser, toUser, len);
    }


    @Override
    public String toString() {
        return "RecvFilePartMsg{" +
                "uuid='" + uuid + '\'' +
                ", filename='" + filename + '\'' +
                ", toUser='" + toUser + '\'' +
                ", fromUser='" + fromUser + '\'' +
                ", spos=" + spos +
                ", len=" + len +
                ", serialNum=" + serialNum +
                ", over=" + over +
                '}';
    }
}
