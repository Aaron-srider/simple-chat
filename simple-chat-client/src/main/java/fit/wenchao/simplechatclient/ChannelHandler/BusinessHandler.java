package fit.wenchao.simplechatclient.ChannelHandler;

import fit.wenchao.simplechatparent.utils.SyncPrinterHelper;
import fit.wenchao.simplechatparent.concurrent.ThreadPool;
import fit.wenchao.simplechatparent.model.business.SendMsgReq;
import fit.wenchao.simplechatparent.proto.IBusinessType;
import fit.wenchao.simplechatparent.proto.IProtoMessage;
import fit.wenchao.simplechatparent.service.FileTransferService;
import fit.wenchao.simplechatparent.utils.DateUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

import static fit.wenchao.simplechatparent.proto.BusinessTypes.*;

@Component
@Slf4j
public class BusinessHandler
{

    public static void main(String[] args)
    {
        ThreadPool.getSingleton().submit(() -> {
            while(true) {
                try
                {
                    Thread.sleep(100);
                    System.out.println("jlkasf");
                } catch (InterruptedException e)
                {
                }
            }
        });

        ThreadPool.getSingleton().shutdownNow();
        ExecutorService executorService = Executors.newCachedThreadPool();
    }

    @Autowired
    @Resource(name = "loginSyncQueue")
    SynchronousQueue<Object> loginRespQueue;

    @Autowired
    FileTransferService fileTransferService;

    @Autowired
    @Resource(name = "loginCountDownLatch")
    CountDownLatch loginCountDownLatch;

    public void handle(ChannelHandlerContext ctx, IProtoMessage msg){
        log.debug("recv server msg: {}", msg);
        IBusinessType businessType = msg.getBusinessType();

        if (businessType.equals(LOGIN_RESP))
        {
            try
            {
                loginRespQueue.put(msg.getMessageData());
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        else if (businessType.equals(SEND_MSG))
        {
            SendMsgReq messageData = (SendMsgReq) msg.getMessageData();
            String now = DateUtils.formatDate(new Date());
            String info = "\n" + now + "   ";
            info+=messageData.getFromUser();
            SyncPrinterHelper.Printer printer = SyncPrinterHelper.getSingleton().lock();
            printer.println(info);
            printer.println(messageData.getText());
            printer.print(">");
            SyncPrinterHelper.getSingleton().unlock();
        }
        else if (businessType.equals(FILE_TRANS_REQ))
        {
            fileTransferService.clientBRecvFileTransferReq(
                    msg,
                    ctx
            );
        }
        else if (businessType.equals(FILE_TRANS_REQ_RESP))
        {
            fileTransferService.clientARecvFileTransferReqResp(
                    msg,
                    ctx
            );
        }
        else if (businessType.equals(SEND_FILE_PACKAGE))
        {
            ThreadPool.getSingleton().submit(() ->
            {
                fileTransferService.clientBRecvFilePackage(msg, ctx);
            });
        }
        else if (businessType.equals(RECV_FILE_PACKAGE_RESP))
        {
            fileTransferService.clientARecvFileTransferResp(msg,
                    ctx
            );
        }
        else if (businessType.equals(TELL_CLIENT_B_2_FINISH))
        {
            fileTransferService.clientBRecvFinishedNotify(msg);
        }
    }
}
