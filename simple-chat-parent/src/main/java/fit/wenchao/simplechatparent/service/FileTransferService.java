package fit.wenchao.simplechatparent.service;

import fit.wenchao.simplechatparent.concurrent.ThreadPool;
import fit.wenchao.simplechatparent.constants.FileSizes;
import fit.wenchao.simplechatparent.constants.RespCodes;
import fit.wenchao.simplechatparent.dao.IUserDao;
import fit.wenchao.simplechatparent.dao.RecvTransferTaskDao;
import fit.wenchao.simplechatparent.dao.SendFileTaskDao;
import fit.wenchao.simplechatparent.model.RecvTransferFileTask;
import fit.wenchao.simplechatparent.model.SendFilePackRecord;
import fit.wenchao.simplechatparent.model.UserPO;
import fit.wenchao.simplechatparent.model.business.*;
import fit.wenchao.simplechatparent.proto.BusinessTypes;
import fit.wenchao.simplechatparent.proto.IMessageData;
import fit.wenchao.simplechatparent.proto.IProtoMessage;
import fit.wenchao.simplechatparent.proto.ProtoMessage;
import fit.wenchao.simplechatparent.utils.FileSegReadCtx;
import fit.wenchao.simplechatparent.utils.FileUtils;
import fit.wenchao.simplechatparent.utils.SyncPrinterHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static fit.wenchao.simplechatparent.utils.StrUtils.filePathBuilder;
import static fit.wenchao.simplechatparent.utils.StrUtils.ft;

/**
 * <p>
 * File transferring can be divided into 3 parts:
 * <pre>
 * 1. Async: Client A ----> "file package" ----> Server ----> "file package" ----> Client B</li>
 * Client B saves file package data to local temp file</li>
 * 2. Client A <---- "ack" <---- Server <---- "ack" <---- Client B</li>
 * Client A marks the corresponding file segment as transferred</li>
 * Repeat the above steps until Client A receives all acks</li>
 * 3. Client A ----> "finish notify" ----> Server ----> "finish notify" ----> Client B</li>
 * Client B does some finishing work</li>
 * </pre>
 * </p>
 */
@Slf4j
@Component
public class FileTransferService {

    @Autowired
    IUserDao userDao;

    @Autowired
    RecvTransferTaskDao recvTransferTaskDao;

    @Autowired
    SendFileTaskDao sendFileTaskDao;

    @Autowired
    ISessionService sessionService;

    Object fileNameDeciderLock = new Object();

    private String getFileName(String oriFileName) {
        // the actual store filename
        String storeFileName = oriFileName;
        File file = new File(oriFileName);
        // this is the first seg, we need to avoid filename duplicated
        String strippedFileName = FileUtils.stripNameSuffix(oriFileName);
        String fileSuffix = FileUtils.getFileSuffix(oriFileName);
        file = new File(oriFileName);
        int count = 1;
        storeFileName = oriFileName;
        synchronized (fileNameDeciderLock) {
            while (file.exists()) {
                if ("".equals(fileSuffix)) {
                    //    file has no suffix
                    storeFileName = ft("{}({})", strippedFileName, count++);
                }
                else {
                    //    file has a suffix
                    storeFileName = ft("{}({}).{}", strippedFileName, count++, fileSuffix);
                }
                file = new File(storeFileName);
            }
        }

        return storeFileName;
    }


    public void clientASendFileTransferReq(String uuid,
                                           String filePath,
                                           String filename,
                                           long totalLen,
                                           String from, String to,
                                           ChannelHandlerContext ctx
    ) {
        FileTransferReq fileTransferReq = FileTransferReq.builder()
                .filename(filename)
                .totalLen(totalLen)
                .filePath(filePath)
                .from(from)
                .to(to)
                .uuid(uuid).build();

        ProtoMessage protoMessage = ProtoMessage.builder()
                .businessType(BusinessTypes.FILE_TRANS_REQ)
                .messageData(fileTransferReq).build();

        log.debug("Send File Transfer Request: {} -> {}", from, to);
        ctx.writeAndFlush(protoMessage);
    }

    public void serverTransitFileTransferRequest(IProtoMessage msg) {
        IProtoMessage protoMessage = msg;
        FileTransferReq fileTransferReq = (FileTransferReq) msg.getMessageData();

        String from = fileTransferReq.getFrom();
        String to = fileTransferReq.getTo();

        UserPO fromUserPO = userDao.getUserByUsername(from);
        UserPO toUserPO = userDao.getUserByUsername(to);

        if (fromUserPO == null) {
            // TODO: tell client to stop transfer
            throw new RuntimeException("From User not Exists");
        }

        if (toUserPO == null) {
            // TODO: tell client to stop transfer
            throw new RuntimeException("To User not Exists");
        }

        Channel fromOnlineChannel = sessionService.getOnlineChannel(from);
        Channel toOnlineChannel = sessionService.getOnlineChannel(to);

        if (fromOnlineChannel == null) {
            throw new RuntimeException("From not Online");
        }

        if (toOnlineChannel == null) {
            throw new RuntimeException("To not Online");
        }

        toOnlineChannel.writeAndFlush(msg);
    }

    public void clientBRecvFileTransferReq(IProtoMessage msg,
                                           ChannelHandlerContext ctx) {
        IMessageData messageData = msg
                .getMessageData();
        FileTransferReq fileTransferReq = (FileTransferReq) messageData;

        String filename = fileTransferReq.getFilename();
        long totalLen = fileTransferReq.getTotalLen();
        String uuid = fileTransferReq.getUuid();
        String from = fileTransferReq.getFrom();
        String to = fileTransferReq.getTo();
        String filePath = fileTransferReq.getFilePath();

        SyncPrinterHelper.Printer printer = SyncPrinterHelper.getSingleton().lock();
        printer.println(
                ft("Receive File: {} From: {}", filename, from));
        printer.print(">");
        SyncPrinterHelper.getSingleton().unlock();

        log.debug("write file part in new thread: {}", Thread.currentThread().getName());

        // create empty temp file with size totalFileSize
        String nowDir = System.getProperty("user.dir");
        String tempFilePath = filePathBuilder().ct(nowDir).ct(uuid).build();
        File tempFile = new File(tempFilePath);
        if (tempFile.exists()) {
            throw new RuntimeException(ft("File exists: {}", tempFilePath));
        }
        FileUtils.fillFile(tempFile, totalLen);

        // new task file
        RandomAccessFile rw = null;
        try {
            rw = new RandomAccessFile(tempFilePath, "rw");
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: tell A to stop transfer
            throw new RuntimeException("File Transfer Error");
        }
        tasks.put(uuid, rw);

        // record new task
        RecvTransferFileTask transferFileTask;
        transferFileTask = new RecvTransferFileTask();
        transferFileTask
                .setUuid(uuid)
                .setTempFilePath(tempFilePath)
                .setFilename(filename)
                .setTotalLen(totalLen)
        ;
        recvTransferTaskDao.add(transferFileTask);

        // resp to server
        FileTransferReqResp fileTransferResp = FileTransferReqResp.builder()
                .from(from)
                .to(to)
                .filePath(filePath)
                .code(RespCodes.SUCCESS.getCode())
                .totalLen(totalLen)
                .uuid(uuid).build();

        ProtoMessage protoMessage = ProtoMessage.builder()
                .businessType(BusinessTypes.FILE_TRANS_REQ_RESP)
                .messageData(fileTransferResp).build();

        log.debug("Client B Recv File Transfer Request");
        ctx.writeAndFlush(protoMessage);
    }

    public void serverTransitFileTransferReqResp(IProtoMessage msg) {
        IProtoMessage protoMessage = msg;
        FileTransferReqResp fileTransferResp = (FileTransferReqResp) msg.getMessageData();

        String from = fileTransferResp.getFrom();
        String to = fileTransferResp.getTo();

        UserPO fromUserPO = userDao.getUserByUsername(from);
        UserPO toUserPO = userDao.getUserByUsername(to);

        if (fromUserPO == null) {
            // TODO: tell client to stop transfer
            throw new RuntimeException("From User not Exists");
        }

        if (toUserPO == null) {
            // TODO: tell client to stop transfer
            throw new RuntimeException("To User not Exists");
        }

        Channel fromOnlineChannel = sessionService.getOnlineChannel(from);
        Channel toOnlineChannel = sessionService.getOnlineChannel(to);

        if (fromOnlineChannel == null) {
            throw new RuntimeException("From not Online");
        }

        if (toOnlineChannel == null) {
            throw new RuntimeException("To not Online");
        }

        fromOnlineChannel.writeAndFlush(msg);
    }

    public void clientARecvFileTransferReqResp(IProtoMessage msg,
                                               ChannelHandlerContext ctx
    ) {

        IMessageData messageData = msg.getMessageData();
        FileTransferReqResp fileTranferResp = (FileTransferReqResp) messageData;
        String code = fileTranferResp.getCode();
        String uuid = fileTranferResp.getUuid();
        if (code.equals(RespCodes.SUCCESS.getCode())) {
            ThreadPool.getSingleton().submit(() -> {
                clientASendFilePackage(fileTranferResp, ctx,
                        FileSizes.ONE_M / 2);
            });
        }
        else {
            System.out.println("File Transfer Request Failed.");
        }

    }

    public void clientASendFilePackage(
            IMessageData msg,
            ChannelHandlerContext ctx,
            long SEG_LEN) {
        FileTransferReqResp fileTransferReqResp = (FileTransferReqResp) msg;
        String filePath = fileTransferReqResp.getFilePath();
        String myName = fileTransferReqResp.getFrom();
        String toName = fileTransferReqResp.getTo();
        String uuid = fileTransferReqResp.getUuid();
        long fileLen = fileTransferReqResp.getTotalLen();

        log.debug("File Transfer Stage << 1 >>");

        // get target file
        File targetFile = new File(filePath);

        // file not exists
        if (!targetFile.exists()) {
            System.out.println(ft("File not exists: {}", filePath));
            return;
        }

        if (fileLen != targetFile.length()) {
            System.out.println(ft("File size not correct, expected: {}, actual", fileLen, targetFile.length()));
            return;
        }

        long lastSize = 0;

        long totalSendCount = fileLen / SEG_LEN;

        if (fileLen % SEG_LEN != 0) {
            lastSize = fileLen - totalSendCount * SEG_LEN;
            totalSendCount++;
        }

        boolean[] finishedList = new boolean[(int) totalSendCount];
        for (int i = 0; i < finishedList.length; i++) {
            finishedList[i] = false;
        }

        long[] posList = new long[(int) totalSendCount];
        int i = 0;
        for (i = 0; i < posList.length; i++) {
            posList[i] = i * SEG_LEN;
        }

        long[] sizeList = new long[(int) totalSendCount];
        for (i = 0; i < sizeList.length - 1; i++) {
            sizeList[i] = SEG_LEN;
        }
        sizeList[i] = lastSize;

        String filename = targetFile.getName();

        SendFilePackRecord sendFilePackRecord = SendFilePackRecord.builder()
                .finished(finishedList)
                .uuid(uuid).build();

        sendFileTaskDao.add(sendFilePackRecord);
        //finishedMap.put(uuid, sendFilePackRecord);

        for (long l = 0; l < totalSendCount; l++) {
            long pos = posList[(int) l];
            long size = sizeList[(int) l];
            boolean finished = finishedList[(int) l];

            FileSegReadCtx fileSegReadCtx = null;
            try {
                fileSegReadCtx = FileUtils.readFileSeg(size, pos, targetFile);
                byte[] bytes = fileSegReadCtx.getBytes();
                //for (byte b : bytes){
                //    System.out.print(b + " ");
                //}
                //System.out.println();
                FilePackage filePackage = new FilePackage();
                filePackage
                        .setUuid(uuid)
                        .setTotal(fileLen)
                        .setLen(bytes.length)
                        .setFilename(filename)
                        .setBytes(bytes)
                        .setSpos(pos)
                        .setFromUser(myName)
                        .setToUser(toName)
                        .setSerialNum(l)
                ;
                ProtoMessage protoMessage = new ProtoMessage();
                protoMessage
                        .setBusinessType(BusinessTypes.SEND_FILE_PACKAGE)
                        .setMessageData(filePackage);

                ctx.writeAndFlush(protoMessage);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Send File error.");
            }

        }


        //// await for transfer finish
        //try {
        //    log.debug("Await for transfer finish");
        //    fileTransferCountDownLatch.await();
        //    log.debug("Input Thread wake up");
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
        // Back to Input Thread
    }

    public void serverTransitFileData(IProtoMessage msg) {
        IProtoMessage protoMessage = msg;
        FilePackage filePackage = (FilePackage) msg.getMessageData();

        String from = filePackage.getFromUser();
        String to = filePackage.getToUser();

        UserPO fromUserPO = userDao.getUserByUsername(from);
        UserPO toUserPO = userDao.getUserByUsername(to);

        if (fromUserPO == null) {
            // TODO: tell client to stop transfer
            throw new RuntimeException("From User not Exists");
        }

        if (toUserPO == null) {
            // TODO: tell client to stop transfer
            throw new RuntimeException("To User not Exists");
        }

        Channel fromOnlineChannel = sessionService.getOnlineChannel(from);
        Channel toOnlineChannel = sessionService.getOnlineChannel(to);

        if (fromOnlineChannel == null) {
            throw new RuntimeException("From not Online");
        }

        if (toOnlineChannel == null) {
            throw new RuntimeException("To not Online");
        }

        toOnlineChannel.writeAndFlush(msg);
    }

    private static final Map<String, RandomAccessFile> tasks = new ConcurrentHashMap<>();

    /**
     * Client B receive file package, write package data to empty file, and reply
     * to Client A with the result.
     */
    public void clientBRecvFilePackage(
            IProtoMessage msg,
            ChannelHandlerContext ctx) {
        log.debug("Send File Stage << 2 >>");
        IMessageData messageData = msg
                .getMessageData();
        FilePackage filePackage = (FilePackage) messageData;
        log.info(filePackage.getSerialNum() + " received");

        // check task exists

        String taskUuid = filePackage.getUuid();
        RecvTransferFileTask transferFileTask = recvTransferTaskDao.get(taskUuid);
        if (transferFileTask == null) {
            throw new RuntimeException("Transfer File Error!");
        }

        // write seg data
        RandomAccessFile randomAccessFile = tasks.get(taskUuid);
        if (randomAccessFile == null) {
            throw new RuntimeException("File Transfer Faild");
        }
        try {
            randomAccessFile.seek(filePackage.getSpos());
            randomAccessFile.write(filePackage.getBytes(), 0, (int) filePackage.getLen());
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.debug("client recv file data successfully: {}, Spos: {}, len: {}"
                , transferFileTask.getTempFilePath()
                , filePackage.getSpos()
                , filePackage.getLen());

        // accumulate recved bytes
        recvTransferTaskDao.addRecved(taskUuid, filePackage.getLen());

        // tell client A recv successfully
        ProtoMessage protoMessage = new ProtoMessage();
        RecvFilePackageResp recvFilePackResp = new RecvFilePackageResp();
        recvFilePackResp
                .setCode(RespCodes.SUCCESS.getCode())
                .setUuid(filePackage.getUuid())
                .setFrom(filePackage.getFromUser())
                .setTo(filePackage.getToUser())
                .setSerialNum(filePackage.getSerialNum())
        ;
        protoMessage.setMessageData(recvFilePackResp)
                .setBusinessType(BusinessTypes.RECV_FILE_PACKAGE_RESP);
        ctx.writeAndFlush(protoMessage);
        log.debug("write success, tell client A: {}", recvFilePackResp);

    }

    public void serverTransitRecvFileResp(IProtoMessage msg) {
        IProtoMessage protoMessage = msg;
        RecvFilePackageResp recvFileResp = (RecvFilePackageResp) msg.getMessageData();

        String from = recvFileResp.getFrom();
        String to = recvFileResp.getTo();

        UserPO fromUserPO = userDao.getUserByUsername(from);
        UserPO toUserPO = userDao.getUserByUsername(to);

        if (fromUserPO == null) {
            // TODO: tell client to stop transfer
            throw new RuntimeException("From User not Exists");
        }

        if (toUserPO == null) {
            // TODO: tell client to stop transfer
            throw new RuntimeException("To User not Exists");
        }

        Channel fromOnlineChannel = sessionService.getOnlineChannel(from);
        Channel toOnlineChannel = sessionService.getOnlineChannel(to);

        if (fromOnlineChannel == null) {
            throw new RuntimeException("From not Online");
        }

        if (toOnlineChannel == null) {
            throw new RuntimeException("To not Online");
        }

        fromOnlineChannel.writeAndFlush(msg);
    }

    // TODO
    public void clientARecvFileTransferResp(IProtoMessage msg,
                                            ChannelHandlerContext ctx) {

        IMessageData messageData = msg.getMessageData();
        RecvFilePackageResp recvFilePackageResp = (RecvFilePackageResp) messageData;
        String code = recvFilePackageResp.getCode();
        String uuid = recvFilePackageResp.getUuid();
        long serialNum = recvFilePackageResp.getSerialNum();
        if (code.equals(RespCodes.SUCCESS.getCode())) {
            sendFileTaskDao.finish(uuid, serialNum);
            Boolean finished = sendFileTaskDao.finished(uuid);
            if(finished == null) {
                throw new RuntimeException("Transfer Error");
            }
            if (finished) {
                tellClientBToFinish(recvFilePackageResp,
                        ctx,
                        recvFilePackageResp.getFrom(),
                        recvFilePackageResp.getTo());
            }
        }

    }

    /**
     * Send a notification to Client B to tell it the transferring is over.
     */
    private void tellClientBToFinish(RecvFilePackageResp recvFilePackageResp,
                                     ChannelHandlerContext ctx,
                                     String from,
                                     String to) {
        FileTransferFinishedNotify fileTransferFinishedNotify = new FileTransferFinishedNotify();
        fileTransferFinishedNotify.setUuid(recvFilePackageResp.getUuid())
                .setFrom(from)
                .setTo(to);

        ProtoMessage protoMessage = new ProtoMessage();
        protoMessage.setBusinessType(BusinessTypes.TELL_CLIENT_B_2_FINISH)
                .setMessageData(fileTransferFinishedNotify);

        ctx.writeAndFlush(protoMessage);
    }

    public void serverTransitFinishNotify(IProtoMessage msg) {
        IProtoMessage protoMessage = msg;
        FileTransferFinishedNotify finishedNotify = (FileTransferFinishedNotify) msg.getMessageData();

        String from = finishedNotify.getFrom();
        String to = finishedNotify.getTo();

        UserPO fromUserPO = userDao.getUserByUsername(from);
        UserPO toUserPO = userDao.getUserByUsername(to);

        if (fromUserPO == null) {
            // TODO: tell client to stop transfer
            throw new RuntimeException("From User not Exists");
        }

        if (toUserPO == null) {
            // TODO: tell client to stop transfer
            throw new RuntimeException("To User not Exists");
        }

        Channel fromOnlineChannel = sessionService.getOnlineChannel(from);
        Channel toOnlineChannel = sessionService.getOnlineChannel(to);

        if (fromOnlineChannel == null) {
            throw new RuntimeException("From not Online");
        }

        if (toOnlineChannel == null) {
            throw new RuntimeException("To not Online");
        }

        toOnlineChannel.writeAndFlush(msg);
    }

    /**
     * Client B is notified to finish the transferring, do some finishing touches.
     * Renaming the temp file name to filename for example.
     */
    public void clientBRecvFinishedNotify(IProtoMessage msg) {
        IProtoMessage protoMessage = msg;
        FileTransferFinishedNotify finishedNotify = (FileTransferFinishedNotify) msg.getMessageData();

        String from = finishedNotify.getFrom();
        String to = finishedNotify.getTo();

        String uuid = finishedNotify.getUuid();

        RecvTransferFileTask transferFileTask = recvTransferTaskDao.get(uuid);
        if (transferFileTask == null) {
            throw new RuntimeException("File Transfer failed");
        }

        String filename = transferFileTask.getFilename();
        String tempFilePath = transferFileTask.getTempFilePath();

        String storeFileName = getFileName(filename);

        File storeFile = new File(storeFileName);
        if (storeFile.exists()) {
            throw new RuntimeException(ft("File Exists, Your Final File Name is: {}", storeFileName));
        }

        File tempFile = new File(tempFilePath);
        if (!tempFile.exists()) {
            throw new RuntimeException(ft("File Transfer Error"));
        }

        try {
            org.apache.commons.io.FileUtils.moveFile(tempFile, storeFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(ft("File Transfer Error"));
        }


        RandomAccessFile randomAccessFile = tasks.get(uuid);
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
                log.info("File Transfer Over, Close File: {}", filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        recvTransferTaskDao.remove(uuid);
    }


    /**
     * Used to mark whether the transfer of file fragments is over.
     * Data access must be synchronized.
     */


}


