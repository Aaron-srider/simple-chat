package fit.wenchao.simplechatparent.service;

import fit.wenchao.simplechatparent.model.RecvTransferFileTask;
import fit.wenchao.simplechatparent.model.business.FilePackage;
import fit.wenchao.simplechatparent.proto.BusinessTypes;
import fit.wenchao.simplechatparent.proto.ProtoMessage;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import static fit.wenchao.simplechatparent.utils.StrUtils.ft;

class FileTransferServiceTest {

    ChannelHandlerContext channelHandlerContext = new ChannelHandlerContext() {
        @Override
        public Channel channel() {
            return null;
        }

        @Override
        public EventExecutor executor() {
            return null;
        }

        @Override
        public String name() {
            return null;
        }

        @Override
        public ChannelHandler handler() {
            return null;
        }

        @Override
        public boolean isRemoved() {
            return false;
        }

        @Override
        public ChannelHandlerContext fireChannelRegistered() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelUnregistered() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelActive() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelInactive() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
            return null;
        }

        @Override
        public ChannelHandlerContext fireUserEventTriggered(Object evt) {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelRead(Object msg) {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelReadComplete() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelWritabilityChanged() {
            return null;
        }

        @Override
        public ChannelHandlerContext read() {
            return null;
        }

        @Override
        public ChannelHandlerContext flush() {
            return null;
        }

        @Override
        public ChannelPipeline pipeline() {
            return null;
        }

        @Override
        public ByteBufAllocator alloc() {
            return null;
        }

        @Override
        public <T> Attribute<T> attr(AttributeKey<T> key) {
            return null;
        }

        @Override
        public <T> boolean hasAttr(AttributeKey<T> key) {
            return false;
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
            return null;
        }

        @Override
        public ChannelFuture disconnect() {
            return null;
        }

        @Override
        public ChannelFuture close() {
            return null;
        }

        @Override
        public ChannelFuture deregister() {
            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture disconnect(ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture close(ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture deregister(ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture write(Object msg) {
            return null;
        }

        @Override
        public ChannelFuture write(Object msg, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object msg) {
            System.out.println(ft("writing: {}", msg));
            return null;
        }

        @Override
        public ChannelPromise newPromise() {
            return null;
        }

        @Override
        public ChannelProgressivePromise newProgressivePromise() {
            return null;
        }

        @Override
        public ChannelFuture newSucceededFuture() {
            return null;
        }

        @Override
        public ChannelFuture newFailedFuture(Throwable cause) {
            return null;
        }

        @Override
        public ChannelPromise voidPromise() {
            return null;
        }
    };

    @Test
    void clientBRecvFilePackage() throws IOException {
        FileTransferService fileTransferService = new FileTransferService();

        Map<String, RecvTransferFileTask> processingTaskUuidList = new ConcurrentHashMap<>();
        FilePackage filePackage;
        ProtoMessage protoMessage;
        String uuid = "123456";

        // 3 nd
        filePackage = new FilePackage();
        filePackage.setFilename("test.txt")
                .setUuid(uuid)
                .setTotal(8)
                .setBytes(new byte[]{48 + 7, 48 + 8})
                .setLen(2)
                .setSpos(6)
        ;
        protoMessage = new ProtoMessage();
        protoMessage.setMessageData(filePackage)
                .setBusinessType(BusinessTypes.SEND_FILE_PACKAGE);
        fileTransferService.clientBRecvFilePackage(protoMessage,
                channelHandlerContext
        );


        // 2 nd
        filePackage = new FilePackage();
        filePackage.setFilename("test.txt")
                .setUuid(uuid)
                .setTotal(8)
                .setBytes(new byte[]{48 + 4, 48 + 5, 48 + 6})
                .setLen(3)
                .setSpos(3)
        ;
        protoMessage = new ProtoMessage();
        protoMessage.setMessageData(filePackage)
                .setBusinessType(BusinessTypes.SEND_FILE_PACKAGE);
        fileTransferService.clientBRecvFilePackage(protoMessage,
                channelHandlerContext
        );


        // 1 st
        filePackage = new FilePackage();
        filePackage.setFilename("test.txt")
                .setUuid(uuid)
                .setTotal(8)
                .setBytes(new byte[]{48 + 1, 48 + 2, 48 + 3})
                .setLen(3)
                .setSpos(0)
        ;
        protoMessage = new ProtoMessage();
        protoMessage.setMessageData(filePackage)
                .setBusinessType(BusinessTypes.SEND_FILE_PACKAGE);
        fileTransferService.clientBRecvFilePackage(protoMessage,
                channelHandlerContext
        );





    }

    @Test
    void sendFilePackage() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String testfilePath = "/home/chaowen/myfiles/projects/java/simple-chat/simple-chat-client/testfile";
        FileTransferService fileTransferService = new FileTransferService();
        //fileTransferService.clientASendFilePackage(testfilePath,
        //        channelHandlerContext,
        //        "wc",
        //        "ls",
        //        countDownLatch,
        //        3,
        //        null);
    }
}