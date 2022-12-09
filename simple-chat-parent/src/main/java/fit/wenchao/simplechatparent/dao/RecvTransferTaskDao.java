package fit.wenchao.simplechatparent.dao;

import fit.wenchao.simplechatparent.model.RecvTransferFileTask;
import org.springframework.stereotype.Component;

@Component
public class RecvTransferTaskDao extends MemMapDao<String, RecvTransferFileTask> {
    public String getPrimaryKeyName(){
        return "uuid";
    }

    public void addRecved(String taskUuid, long len)
    {
        RecvTransferFileTask recvTransferFileTask = get(taskUuid);
        if(recvTransferFileTask!=null) {
            recvTransferFileTask.addRecved(len);
            update(recvTransferFileTask);
        }
    }
}