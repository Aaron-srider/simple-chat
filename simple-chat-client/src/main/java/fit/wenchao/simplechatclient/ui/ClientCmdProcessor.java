package fit.wenchao.simplechatclient.ui;

import fit.wenchao.simplechatclient.utils.ProgressBar;
import fit.wenchao.simplechatclient.utils.RecvTaskListItem;
import fit.wenchao.simplechatclient.utils.SyncPrinterHelper;
import fit.wenchao.simplechatclient.utils.Table;
import fit.wenchao.simplechatparent.dao.RecvTransferTaskDao;
import fit.wenchao.simplechatparent.model.RecvTransferFileTask;
import fit.wenchao.simplechatparent.model.business.SendMsgReq;
import fit.wenchao.simplechatparent.proto.ProtoMessage;
import fit.wenchao.simplechatparent.service.FileTransferService;
import fit.wenchao.simplechatparent.utils.cmd.Cli;
import fit.wenchao.simplechatparent.utils.cmd.CommandParser;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static fit.wenchao.simplechatclient.utils.Table.getAttrFromEntity;
import static fit.wenchao.simplechatparent.proto.BusinessTypes.SEND_TEXT_REQ;
import static fit.wenchao.simplechatparent.utils.StrUtils.ft;

@Slf4j
@Component
public class ClientCmdProcessor
{

    @Autowired
    FileTransferService fileTransferService;

    @Autowired
    RecvTransferTaskDao recvTransferTaskDao;

    @Autowired
    CommandParser commandParser;

    public void processCmd(String cmd, String username, ChannelHandlerContext ctx)
    {
        Cli cli = commandParser.parse(cmd);
        String cmdName = cli.getCmdName();

        switch (cmdName)
        {
            case "send":
            {
                // get target username
                if (!cli.contains("u"))
                {
                    //throw new CliArgMissingException("user not specified");
                    System.out.println("user not specified");
                    return;
                }

                String user = cli.get("u");
                // get send text body
                if (cli.contains("t"))
                {
                    //String msg = cli.get("t");
                    //log.debug("send msg: {}, to: {}", msg, user);

                    Scanner msgScanner = new Scanner(System.in);
                    String msg = "";
                    String line  = "";
                    line = msgScanner.nextLine();
                    while(true) {
                        msg += "\n";
                        msg += line;
                        line = msgScanner.nextLine();
                        if(line.endsWith("@@")) {
                            break;
                        }
                    }

                    ProtoMessage sendMsg = new ProtoMessage();
                    SendMsgReq sendMsgReq = new SendMsgReq();
                    sendMsgReq.setFromUser(username);
                    sendMsgReq.setTargetUser(user);
                    sendMsgReq.setText(msg);
                    sendMsg.setBusinessType(SEND_TEXT_REQ);
                    sendMsg.setMessageData(sendMsgReq);

                    ctx.writeAndFlush(sendMsg);
                    return;
                }

                // "File Transfer Stage << 1 >>"
                if (cli.contains("f"))
                {
                    String from = username;
                    String to = user;
                    String filePath = cli.get("f");


                    //TODO: request server for this sending, make
                    // sure that user exists.

                    // get target file
                    File targetFile = new File(filePath);

                    // file not exists
                    if (!targetFile.exists())
                    {
                        System.out.println(ft("File not exists: {}", filePath));
                        return;
                    }

                    // new task uuid
                    String uuid = UUID.randomUUID().toString();

                    long fileLen = targetFile.length();

                    fileTransferService.clientASendFileTransferReq(
                            uuid,
                            targetFile.getAbsolutePath(),
                            targetFile.getName(),
                            fileLen,
                            from,
                            to,
                            ctx
                    );
                    return;
                }

                cli.usage();
                break;
            }
            case "list-task":
            {
                List<RecvTaskListItem> tableData = new ArrayList<>();

                List<RecvTransferFileTask> recvTransferFileTaskList = recvTransferTaskDao.list();
                for (RecvTransferFileTask transferFileTask : recvTransferFileTaskList)
                {
                    String uuid = transferFileTask.getUuid();
                    RecvTaskListItem recvTaskListItem = new RecvTaskListItem();
                    long recved = transferFileTask.getRecved();

                    recvTaskListItem
                            .setUuid(uuid)
                            .setTempPath(transferFileTask.getTempFilePath())
                            .setProgress((int) (recved * 100 / transferFileTask.getTotalLen()))
                    ;
                    tableData.add(recvTaskListItem);
                }

                Table.CustomPrintCell<RecvTaskListItem> customPrintCell = new Table.CustomPrintCell<>();
                customPrintCell.setCustom((recv) ->
                {
                    String progress = getAttrFromEntity(recv, "progress");
                    return new ProgressBar().getProgressBar("", Integer.parseInt(progress));
                });
                customPrintCell.setColName("progress");
                List<Table.CustomPrintCell<RecvTaskListItem>> customPrintCells = new ArrayList<>();
                customPrintCells.add(customPrintCell);

                Table<RecvTaskListItem> table = new Table<>();
                table.setGapList(new int[]{4, 4});
                table.setWidthList(new int[]{36, 26});
                table.setHeadList(new String[]{"uuid", "progress", "tempPath"});
                table.setList(tableData);
                table.setCustomColPrintPolicy(customPrintCells);

                SyncPrinterHelper.Printer printer = SyncPrinterHelper.getSingleton().lock();
                table.print(printer);
                SyncPrinterHelper.getSingleton().unlock();
                break;
            }
        }

    }
}
