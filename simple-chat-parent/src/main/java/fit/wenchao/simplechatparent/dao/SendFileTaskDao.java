package fit.wenchao.simplechatparent.dao;

import fit.wenchao.simplechatparent.model.SendFilePackRecord;
import org.springframework.stereotype.Component;

@Component
public class SendFileTaskDao extends MemMapDao<String, SendFilePackRecord>
{
    @Override
    public String getPrimaryKeyName()
    {
        return "uuid";
    }

    public synchronized boolean finish(String uuid, long serialNum) {
        SendFilePackRecord sendFilePackRecord = get(uuid);
        if(sendFilePackRecord!=null) {
            sendFilePackRecord.finish((int) serialNum);
            update(sendFilePackRecord);
            return true;
        }
        return false;
    }

    public synchronized Boolean finished(String uuid) {
        SendFilePackRecord sendFilePackRecord = get(uuid);
        if(sendFilePackRecord!=null) {
            return sendFilePackRecord.finished();
        }
        return null;
    }
}
