package fit.wenchao.simplechatparent.service;

import fit.wenchao.simplechatparent.model.RecvTransferFileTask;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransferFileTaskService implements ITransferFileTaskService {

    private static Map<String, RecvTransferFileTask> transferFilePartTaskMap =
            new ConcurrentHashMap<>();


    @Override
    synchronized public void addTask(RecvTransferFileTask transferFieTask) {
        // check task exists or not
        RecvTransferFileTask transferFieTaskExists = transferFilePartTaskMap.get(transferFieTask.getUuid());
        if (transferFieTaskExists != null) {
            throw new RuntimeException("Transfer task exists");
        }

        // task not exists, add
        transferFilePartTaskMap.put(transferFieTask.getUuid(), transferFieTask);
    }

    @Override
    synchronized public RecvTransferFileTask getTask(String uuid) {
        // check exists
        RecvTransferFileTask transferFileTaskExists = transferFilePartTaskMap.get(uuid);
        if (transferFileTaskExists == null) {
            return null;
        }

        // return copy
        RecvTransferFileTask transferFileTask = new RecvTransferFileTask();
        BeanUtils.copyProperties(transferFileTaskExists, transferFileTask);
        return transferFileTask;
    }


    @Override
    synchronized public void updateTask(RecvTransferFileTask task) {
        // check exists
        RecvTransferFileTask transferFileTaskExists = transferFilePartTaskMap.get(task.getUuid());
        if (transferFileTaskExists == null) {
            return;
        }

        BeanUtils.copyProperties(task, transferFileTaskExists);
    }
}
