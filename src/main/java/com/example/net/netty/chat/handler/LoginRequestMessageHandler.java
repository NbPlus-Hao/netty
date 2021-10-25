package com.example.net.netty.chat.handler;

import com.example.net.netty.chat.message.LoginRequestMessage;
import com.example.net.netty.chat.message.LoginResponseMessage;
import com.example.net.netty.chat.server.ChatServer;
import com.example.net.netty.chat.server.service.UserService;
import com.example.net.netty.chat.server.service.UserServiceFactory;
import com.example.net.netty.chat.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        log.info("用户名：{}  密码：{}", username, password);
        UserService userService = UserServiceFactory.getUserService();
        boolean isLogin = userService.login(username, password);
        LoginResponseMessage message;
        if (isLogin) {
            SessionFactory.getSession().bind(ctx.channel(), username);
            message = new LoginResponseMessage(true, "登录成功");
        } else {
            message = new LoginResponseMessage(false, "用户名或密码错误");
        }
        ctx.writeAndFlush(message);
    }
}
