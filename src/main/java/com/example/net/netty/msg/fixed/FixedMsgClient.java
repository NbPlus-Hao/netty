package com.example.net.netty.msg.fixed;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
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

import java.util.Random;

@Slf4j
public class FixedMsgClient {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            send();
        }
        log.info("finish");
    }

    private static void send() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) {
                                    log.info("sending.......");
                                    Random random = new Random();
                                    char c = 'a';
                                    ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
                                    for (int i = 0; i < 10; i++) {
                                        byte[] bs = fillBytes(c,random.nextInt(10) + 1);
                                        buf.writeBytes(bs);
                                        c++;
                                    }
                                    ctx.writeAndFlush(buf);
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

    private static byte[] fillBytes(char c, int i) {
        byte[] bytes = new byte[10];
        for (int j = 0; j < 10; j++) {
            if (j < i) {
                bytes[j] = (byte) c;
            } else {
                bytes[j] = 0x20;
            }
        }
        return bytes;
    }
}
