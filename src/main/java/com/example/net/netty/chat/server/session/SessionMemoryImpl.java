package com.example.net.netty.chat.server.session;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionMemoryImpl implements Session{

    private final Map<String,Channel> usernameChannelMap = new ConcurrentHashMap<>();
    private final Map<String,String> channelUsernameMap = new ConcurrentHashMap<>();
    private final Map<Channel,Map<String,Object>> channelAttributeMap = new ConcurrentHashMap<>();


    @Override
    public void bind(Channel channel, String username) {

    }

    @Override
    public void unbind(Channel channel) {

    }

    @Override
    public Object getAttribute(Channel channel, String name) {
        return null;
    }

    @Override
    public void setAttribute(Channel channel, String name, Object value) {

    }

    @Override
    public Channel getChannel(String username) {
        return null;
    }
}
