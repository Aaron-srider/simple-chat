package fit.wenchao.simplechatclient.ui;

import fit.wenchao.simplechatparent.constants.RespCodes;
import fit.wenchao.simplechatparent.model.business.LoginReq;
import fit.wenchao.simplechatparent.model.business.LoginResp;
import fit.wenchao.simplechatparent.proto.IProtoMessage;
import fit.wenchao.simplechatparent.proto.ProtoMessage;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Scanner;
import java.util.concurrent.SynchronousQueue;

import static fit.wenchao.simplechatparent.proto.BusinessTypes.LOGIN;

@Component
public class InteractiveThreadTask implements Runnable
{
    @Autowired
    @Resource(name = "loginSyncQueue")
    SynchronousQueue<Object> loginRespQueue;

    int times;

    boolean loseConnToServer;

    public void setLoseConnToServer(boolean loseConnToServer)
    {
        this.loseConnToServer = loseConnToServer;
    }
    //public static void main(String[] args) throws InterruptedException
    //{
    //    SynchronousQueue<String> queue = new SynchronousQueue<>();
    //    new Thread(() ->
    //    {
    //        try
    //        {
    //            Thread.sleep(1000);
    //        } catch (InterruptedException e)
    //        {
    //            e.printStackTrace();
    //        }
    //    }).start();
    //}

    //@Autowired
    //@Resource(name = "loginCountDownLatch")
    //CountDownLatch loginCountDownLatch;

    @Autowired
    ClientCmdProcessor clientCmdProcessor;

    ChannelHandlerContext ctx;

    public void setCtx(ChannelHandlerContext ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public void run()
    {

        // get user username and password
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username: ");
        if(loseConnToServer) {
            // ask user to skip last input manually
            System.out.print("Please press enter to continue");
            loseConnToServer = false;
        }

        String username = scanner.nextLine();

        username = username.trim();
        System.out.println("Password: ");
        String password = scanner.nextLine();
        password = password.trim();

        // send login request
        LoginReq loginReq = LoginReq.builder()
                .password(password)
                .username(username).build();
        IProtoMessage protoMessage = ProtoMessage.builder()
                .messageData(loginReq)
                .businessType(LOGIN).build();
        ctx.channel().writeAndFlush(protoMessage);

        // wait for login successfully
        LoginResp loginResp = null;
        try
        {
            loginResp = (LoginResp) loginRespQueue.take();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        String respCode = loginResp.getCode();
        if(respCode.equals(RespCodes.SUCCESS.getCode())) {
            System.out.println("Hello, " + username);
        } else if(respCode.equals(RespCodes.LOGIN_FAIL.getCode())) {
            System.out.println("Login failed");
            this.run();
        }

        // process user cmd loop
        while (true)
        {
            System.out.print(">");
            String cmd = scanner.nextLine();
            clientCmdProcessor.processCmd(cmd, username, ctx);
        }
    }
}
