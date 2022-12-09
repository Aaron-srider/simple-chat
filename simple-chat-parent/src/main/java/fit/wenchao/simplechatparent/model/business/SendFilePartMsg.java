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
public class SendFilePartMsg implements IMessageData {
    String uuid;
    String filename;
    String path;
    byte[] bytes;
    String fromUser;
    String toUser;
    long spos;
    long len;
    long serialNum;

    boolean over;

    public String reportFilePart() {
        return ft("Pack No.: {}, Last Pack: {}, Pack Route: {} -> {}, Pack Len: {}", serialNum, over, fromUser, toUser, len);
    }

    @Override
    public String toString() {
        return "SendFilePartMsg{" +
                "uuid='" + uuid + '\'' +
                ", filename='" + filename + '\'' +
                ", path='" + path + '\'' +
                ", fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", spos=" + spos +
                ", len=" + len +
                ", serialNum=" + serialNum +
                ", over=" + over +
                '}';
    }
}
