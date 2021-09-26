package com.example.net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class NettyTimeClientHandler extends ChannelHandlerAdapter {
    private final static Logger logger = Logger.getLogger(NettyTimeClientHandler.class.getName());
    private final ByteBuf firstMessage;

    public NettyTimeClientHandler() {
        byte[] req = "QUERY TIME ORDER".getBytes(StandardCharsets.UTF_8);
        firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        System.out.println(new String(bytes));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warning(cause.getMessage());
        ctx.close();
    }
}
