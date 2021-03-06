package com.example.net.netty.chat.client;

import com.example.net.netty.chat.message.*;
import com.example.net.netty.chat.protocol.MessageCodecSharable;
import com.example.net.netty.chat.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {

    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler logger = new LoggingHandler(LogLevel.INFO);
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean login = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtocolFrameDecoder());
//                            ch.pipeline().addLast(logger);
                            ch.pipeline().addLast(messageCodec);
                            ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    new Thread(() -> {
                                        Scanner scanner = new Scanner(System.in);
                                        log.info("??????????????????");
                                        String username = scanner.nextLine();
                                        log.info("??????????????????");
                                        String password = scanner.nextLine();
                                        LoginRequestMessage message = new LoginRequestMessage(username, password);
                                        ctx.writeAndFlush(message);

                                        log.info("??????????????????");
                                        try {
                                            latch.await();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if (!login.get()) {
                                            ctx.close();
                                            return;
                                        }
                                        while (true) {
                                            System.out.println("==================================");
                                            System.out.println("send [username] [content]");
                                            System.out.println("gsend [group name] [content]");
                                            System.out.println("gcreate [group name] [m1,m2,m3...]");
                                            System.out.println("gmembers [group name]");
                                            System.out.println("gjoin [group name]");
                                            System.out.println("gquit [group name]");
                                            System.out.println("quit");
                                            System.out.println("==================================");
                                            String command = scanner.nextLine();
                                            String[] s = command.split(" ");
                                            switch (s[0]){
                                                case "send":
                                                    ctx.writeAndFlush(new ChatRequestMessage(username, s[1], s[2]));
                                                    break;
                                                case "gsend":
                                                    ctx.writeAndFlush(new GroupChatRequestMessage(username, s[1], s[2]));
                                                    break;
                                                case "gcreate":
                                                    Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                                    set.add(username); // ????????????
                                                    ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], set));
                                                    break;
                                                case "gmembers":
                                                    ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                                    break;
                                                case "gjoin":
                                                    ctx.writeAndFlush(new GroupJoinRequestMessage(username, s[1]));
                                                    break;
                                                case "gquit":
                                                    ctx.writeAndFlush(new GroupQuitRequestMessage(username, s[1]));
                                                    break;
                                                case "quit":
                                                    ctx.channel().close();
                                                    return;
                                            }

                                        }
                                    }, "system in").start();
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    log.info("{}", msg);
                                    if (msg instanceof LoginResponseMessage) {
                                        LoginResponseMessage responseMessage = (LoginResponseMessage) msg;
                                        if (responseMessage.isSuccess()) {
                                            login.set(true);
                                        }
                                    }
                                    latch.countDown();
                                }
                            });
                        }
                    });
            ChannelFuture future = bootstrap.connect("localhost", 8080).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }
}
