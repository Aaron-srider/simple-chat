package fit.wenchao.simplechatclient.ChannelHandler;

import fit.wenchao.simplechatclient.NettyClient;
import fit.wenchao.simplechatclient.ui.ClientCmdProcessor;
import fit.wenchao.simplechatclient.ui.InteractiveThreadTask;
import fit.wenchao.simplechatparent.dao.RecvTransferTaskDao;
import fit.wenchao.simplechatparent.proto.IProtoMessage;
import fit.wenchao.simplechatparent.proto.codec.FrameDecoder;
import fit.wenchao.simplechatparent.proto.codec.ProtoCodec;
import fit.wenchao.simplechatparent.service.FileTransferService;
import fit.wenchao.simplechatparent.utils.cmd.CommandParser;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fit.wenchao.simplechatparent.utils.StrUtils.ft;

@Component
@Slf4j
public class ClientChannelInitializer extends ChannelInitializer<NioSocketChannel>
{

    NettyClient client;

    public void setClient(NettyClient client)
    {
        this.client = client;
    }

    @Autowired
    InteractiveThreadTask interactiveThreadTask;

    Thread uiThread;

    @Autowired
    ClientCmdProcessor clientCmdProcessor;

    @Autowired
    BusinessHandler businessHandler;

    @Autowired
    FileTransferService fileTransferService;

    @Autowired
    RecvTransferTaskDao recvTransferTaskDao;

    @Autowired
    ProtoCodec protoCodec;

    @Autowired
    CommandParser commandParser;

    @Override
    protected void initChannel(NioSocketChannel ch)
    {

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new FrameDecoder());
        //pipeline.addLast(new LoggingHandler());
        pipeline.addLast(protoCodec);

        // when connected to server
        pipeline.addLast(new ChannelInboundHandlerAdapter()
        {
            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception
            {
                System.out.println(ft("Lose Contact with Server, Try To Reconnect..."));
                interactiveThreadTask.setLoseConnToServer(true);
                ChannelFuture close = ctx.close();
                uiThread.stop();
                //ThreadPool.getSingleton().reStart();
                close.sync();
                client.connect();
            }

            @Override
            public void channelActive(ChannelHandlerContext ctx)
            {
                interactiveThreadTask.setCtx(ctx);
                // start to interact with user, get cmds and process them
                uiThread = new Thread(interactiveThreadTask);
                uiThread.start();
                //ThreadPool.getSingleton().submit(interactiveThreadTask);
            }
        });

        // process incoming message
        pipeline.addLast(
                new SimpleChannelInboundHandler<IProtoMessage>()
                {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, IProtoMessage msg) throws Exception
                    {
                        businessHandler.handle(ctx, msg);
                    }
                }
        );

    }

}
