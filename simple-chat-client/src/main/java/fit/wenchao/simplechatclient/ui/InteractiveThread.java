package fit.wenchao.simplechatclient.ui;

import fit.wenchao.simplechatparent.model.business.LoginReq;
import fit.wenchao.simplechatparent.proto.IProtoMessage;
import fit.wenchao.simplechatparent.proto.ProtoMessage;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import static fit.wenchao.simplechatparent.proto.BusinessTypes.LOGIN;

@Component
public class InteractiveThread implements Runnable
{
    @Autowired
    @Resource(name = "loginCountDownLatch")
    CountDownLatch loginCountDownLatch;

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

        try
        {
            // wait for login successfully
            loginCountDownLatch.await();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        // process user cmd loop
        while (true)
        {
            String cmd = scanner.nextLine();
            clientCmdProcessor.processCmd(cmd, username, ctx);
        }
    }
}
