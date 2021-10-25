package com.example.net.netty.chat.handler;

import com.example.net.netty.chat.message.ChatRequestMessage;
import com.example.net.netty.chat.message.ChatResponseMessage;
import com.example.net.netty.chat.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        if (channel == null) {
            ctx.writeAndFlush(new ChatResponseMessage(false,"用户不在线或不存在"));
        } else {
            ctx.writeAndFlush(new ChatResponseMessage(msg.getFrom(),msg.getContent()));
        }
    }
}
