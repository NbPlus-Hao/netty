package com.example.net.netty.chat.server;

import com.example.net.netty.chat.message.LoginRequestMessage;
import com.example.net.netty.chat.message.LoginResponseMessage;
import com.example.net.netty.chat.protocol.MessageCodecSharable;
import com.example.net.netty.chat.protocol.ProtocolFrameDecoder;
import com.example.net.netty.chat.server.service.UserService;
import com.example.net.netty.chat.server.service.UserServiceFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatServer {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler logger = new LoggingHandler(LogLevel.INFO);
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtocolFrameDecoder());
//                            ch.pipeline().addLast(logger);
                            ch.pipeline().addLast(messageCodec);
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<LoginRequestMessage>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
                                    String username = msg.getUsername();
                                    String password = msg.getPassword();
                                    log.info("用户名：{}  密码：{}", username, password);
                                    UserService userService = UserServiceFactory.getUserService();
                                    boolean isLogin = userService.login(username, password);
                                    LoginResponseMessage message;
                                    if (isLogin) {
                                        message = new LoginResponseMessage(true, "登录成功");
                                    } else {
                                        message = new LoginResponseMessage(false, "用户名或密码错误");
                                    }
                                    ctx.writeAndFlush(message);
                                }
                            });
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(8080).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
