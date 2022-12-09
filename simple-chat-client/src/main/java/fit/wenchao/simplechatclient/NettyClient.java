package fit.wenchao.simplechatclient;

import fit.wenchao.simplechatclient.ChannelHandler.ClientChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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
    EventLoopGroup eventLoopGroup;
    Bootstrap bootstrap;

    public void init()
    {
        eventLoopGroup = new NioEventLoopGroup();
        clientChannelInitializer.setClient(this);
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(clientChannelInitializer);
    }

    public void connect() {
        ChannelFuture channelFuture = bootstrap.connect("localhost", 8076);
        channelFuture.addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception
            {
                if (!future.isSuccess())
                {
                    future.channel().eventLoop().schedule(() ->
                    {
                        try
                        {
                            System.out.println("Connect Server Failed, Reconnect in 3 Seconds...");
                            connect();
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }, 3000, TimeUnit.MILLISECONDS);
                }
                else
                {
                    System.out.println("Connect Server Success");
                }
            }
        });

        try
        {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void start()
    {

        eventLoopGroup = new NioEventLoopGroup();

        try
        {
            clientChannelInitializer.setClient(this);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(clientChannelInitializer);
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8076);
            channelFuture.addListener(new ChannelFutureListener()
            {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception
                {
                    if (!future.isSuccess())
                    {
                        future.channel().eventLoop().schedule(() ->
                        {
                            try
                            {
                                System.out.println("Connect Server Failed, Reconnect in 3 Seconds...");
                                start();
                            } catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }, 3000, TimeUnit.MILLISECONDS);
                    }
                    else
                    {
                        System.out.println("Connect Server Success");
                    }
                }
            });
            while (true)
            {
                Thread.sleep(500);
            }
            //channelFuture.sync();
            //Channel channel = channelFuture.channel();
            //channel.closeFuture().sync();
        } catch (Exception e)
        {
            //e.printStackTrace();
            //start();
            System.out.println("in catch");
        } finally
        {
            eventLoopGroup.shutdownGracefully();
            System.out.println("in finally");
        }

        System.out.println("out try");
    }

}
