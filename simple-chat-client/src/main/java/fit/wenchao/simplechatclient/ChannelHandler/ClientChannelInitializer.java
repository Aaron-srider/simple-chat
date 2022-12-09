package fit.wenchao.simplechatclient.ChannelHandler;

import fit.wenchao.simplechatclient.ui.InteractiveThread;
import fit.wenchao.simplechatclient.ui.ClientCmdProcessor;
import fit.wenchao.simplechatparent.concurrent.ThreadPool;
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

@Component
@Slf4j
public class ClientChannelInitializer extends ChannelInitializer<NioSocketChannel>
{

    @Autowired
    InteractiveThread interactiveThread;

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
            public void channelActive(ChannelHandlerContext ctx)
            {
                interactiveThread.setCtx(ctx);
                // start to interact with user, get cmds and process them
                ThreadPool.getSingleton().submit(interactiveThread);
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
