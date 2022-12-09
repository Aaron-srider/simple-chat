package fit.wenchao.simplechatserver;

import fit.wenchao.simplechatparent.constants.BeanNameConstants;
import fit.wenchao.simplechatparent.constants.RespCodes;
import fit.wenchao.simplechatparent.dao.IUserDao;
import fit.wenchao.simplechatparent.model.UserPO;
import fit.wenchao.simplechatparent.model.business.LoginReq;
import fit.wenchao.simplechatparent.model.business.ReceiveMsgResp;
import fit.wenchao.simplechatparent.model.business.SendMsgReq;
import fit.wenchao.simplechatparent.model.business.SendMsgResp;
import fit.wenchao.simplechatparent.proto.*;
import fit.wenchao.simplechatparent.proto.codec.FrameDecoder;
import fit.wenchao.simplechatparent.proto.codec.ProtoCodec;
import fit.wenchao.simplechatparent.service.FileTransferService;
import fit.wenchao.simplechatparent.service.ISessionService;
import fit.wenchao.simplechatparent.service.ITransferFileTaskService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static fit.wenchao.simplechatparent.constants.RespCodes.LOGIN_FAIL;
import static fit.wenchao.simplechatparent.constants.RespCodes.SUCCESS;
import static fit.wenchao.simplechatparent.proto.BusinessTypes.LOGIN_RESP;


@Component(BeanNameConstants.SERVER_BEAN_NAME)
@Slf4j
public class NettyServer {

    @Autowired
    FileTransferService fileTransferService;

    @Value("${netty.config.server.port}")
    String port;


    @Autowired
    ProtoCodec protoCodec;

    @Autowired
    IUserDao userDao;

    @Autowired
    ISessionService sessionService;

    @Autowired
    ITransferFileTaskService transferFileTaskService;

    public void start() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(eventLoopGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(
                    new ChannelInitializer<NioSocketChannel>() {

                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new FrameDecoder());
                            pipeline.addLast(new LoggingHandler());
                            pipeline.addLast(protoCodec);
                            pipeline.addLast(new SimpleChannelInboundHandler<IProtoMessage>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, IProtoMessage msg) throws Exception {
                                    IBusinessType businessType = msg.getBusinessType();
                                    if (businessType.equals(BusinessTypes.LOGIN)) {
                                        IMessageData messageData = msg.getMessageData();
                                        LoginReq loginReq = (LoginReq) messageData;

                                        log.debug("server recv: {}", loginReq);

                                        String username = loginReq.getUsername();
                                        String passwd = loginReq.getPassword();
                                        UserPO userExists = userDao.getUserByUsernameAndPwd(username, passwd);

                                        // user not exists
                                        if (userExists == UserPO.NULL) {
                                            ProtoMessage protoMessage = new ProtoMessage(new SendMsgResp(LOGIN_FAIL.getCode()), LOGIN_RESP);
                                            ctx.writeAndFlush(protoMessage);
                                            return;
                                        }

                                        // put user online
                                        sessionService.online(userExists.getId(), ctx.channel());

                                        ProtoMessage protoMessage = new ProtoMessage(new SendMsgResp(SUCCESS.getCode()), LOGIN_RESP);
                                        ctx.writeAndFlush(protoMessage);
                                    }
                                    else if (businessType.equals(BusinessTypes.SEND_TEXT_REQ)) {
                                        IMessageData messageData = msg.getMessageData();
                                        SendMsgReq sendMsgReq = (SendMsgReq) messageData;

                                        log.debug("server recv: {}", sendMsgReq);
                                        String fromUser = sendMsgReq.getFromUser();
                                        String targetUser = sendMsgReq.getTargetUser();
                                        String text = sendMsgReq.getText();


                                        UserPO userOnline = sessionService.userOnline(targetUser);
                                        ProtoMessage protoMessage;


                                        // user not online
                                        if (userOnline == UserPO.NULL) {
                                            protoMessage =
                                                    new ProtoMessage(
                                                            new SendMsgResp(RespCodes.TARGET_USER_NOT_ONLINE.getCode()), BusinessTypes.SEND_MSG_RESP);
                                            ctx.writeAndFlush(protoMessage);
                                            return;
                                        }


                                        Channel onlineChannel = sessionService.getOnlineChannel(targetUser);

                                        if (onlineChannel == null) {
                                            protoMessage =
                                                    new ProtoMessage(
                                                            new SendMsgResp(RespCodes.TARGET_USER_NOT_ONLINE.getCode()), BusinessTypes.SEND_MSG_RESP);
                                            ctx.writeAndFlush(protoMessage);
                                            return;
                                        }

                                        // send msg to target user
                                        protoMessage = new ProtoMessage(new ReceiveMsgResp(text), BusinessTypes.RECV_MSG_RESP);
                                        onlineChannel.writeAndFlush(protoMessage);
                                    }
                                    else if (businessType.equals(BusinessTypes.FILE_TRANS_REQ)) {
                                        fileTransferService.serverTransitFileTransferRequest(msg);
                                    }
                                    else if (businessType.equals(BusinessTypes.FILE_TRANS_REQ_RESP)) {
                                        fileTransferService.serverTransitFileTransferReqResp(msg);
                                    }
                                    else if (businessType.equals(BusinessTypes.SEND_FILE_PACKAGE)) {
                                        fileTransferService.serverTransitFileData(msg);
                                    }
                                    else if (businessType.equals(BusinessTypes.RECV_FILE_PACKAGE_RESP)) {
                                        fileTransferService.serverTransitRecvFileResp(msg);
                                    }
                                    else if (businessType.equals(BusinessTypes.TELL_CLIENT_B_2_FINISH)) {
                                        fileTransferService.serverTransitFinishNotify(msg);
                                    }
                                }
                            });
                        }
                    }
            );
            ChannelFuture channelFuture = serverBootstrap.bind(Integer.parseInt(port));
            channelFuture.sync();
            Channel channel = channelFuture.channel();
            ChannelFuture closeFuture = channel.closeFuture();
            closeFuture.sync();

        } catch (
                InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
