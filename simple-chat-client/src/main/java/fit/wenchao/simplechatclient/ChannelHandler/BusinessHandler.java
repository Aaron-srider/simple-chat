package fit.wenchao.simplechatclient.ChannelHandler;

import fit.wenchao.simplechatparent.concurrent.ThreadPool;
import fit.wenchao.simplechatparent.model.business.ReceiveMsgResp;
import fit.wenchao.simplechatparent.proto.IBusinessType;
import fit.wenchao.simplechatparent.proto.IProtoMessage;
import fit.wenchao.simplechatparent.service.FileTransferService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;

import static fit.wenchao.simplechatparent.proto.BusinessTypes.*;

@Component
@Slf4j
public class BusinessHandler
{

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
        else if (businessType.equals(RECV_MSG_RESP))
        {
            ReceiveMsgResp messageData = (ReceiveMsgResp) msg.getMessageData();
            System.out.println(messageData.getText());
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
