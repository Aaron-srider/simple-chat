package fit.wenchao.simplechatparent.model;

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
public class RecvTransferFileTask
{
    String uuid;
    String filename;
    String path;
    long spos;
    long len;
    String fromUser;
    String toUser;

    String tempFilePath ;
    String recvFilePath ;

    long recved = 0 ;

    public synchronized void setRecved(long recved) {
        this.recved = recved;
    }

    public synchronized long getRecved() {
        return recved;
    }

    public synchronized void addRecved(long add) {
        this.recved += add;
    }

    long totalLen;

    long totalSeg;
}
