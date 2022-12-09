package fit.wenchao.simplechatparent.service;


import fit.wenchao.simplechatparent.model.RecvTransferFileTask;

public interface ITransferFileTaskService {
    void addTask(RecvTransferFileTask transferFieTask);

    RecvTransferFileTask getTask(String uuid);

    void updateTask(RecvTransferFileTask task);
}
