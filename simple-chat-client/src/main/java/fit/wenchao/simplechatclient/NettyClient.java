package fit.wenchao.simplechatclient;

import fit.wenchao.simplechatclient.ChannelHandler.ClientChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("client")
@Slf4j
public class NettyClient
{

    @Autowired
    ClientChannelInitializer clientChannelInitializer;

    //public ChannelProgressivePromise getChannelProgressivePromise(
    //        ChannelHandlerContext ctx
    //)
    //{
    //    ChannelProgressivePromise channelProgressivePromise = ctx.newProgressivePromise();
    //    channelProgressivePromise.addListener(new ChannelProgressiveFutureListener()
    //    {
    //        @Override
    //        public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception
    //        {
    //            //System.out.println(total);
    //            addAccumulate(progress);
    //        }
    //
    //        @Override
    //        public void operationComplete(ChannelProgressiveFuture future) throws Exception
    //        {
    //        }
    //    });
    //    return channelProgressivePromise;
    //}

    public void start()
    {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try
        {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(clientChannelInitializer);
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8076);
            channelFuture.sync();
            Channel channel = channelFuture.channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } finally
        {
            eventLoopGroup.shutdownGracefully();
        }
    }

}
