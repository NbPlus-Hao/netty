package com.example.net.netty.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.msgpack.MessagePack;

import java.util.List;

public class MsgPackEncoder extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        MessagePack messagePack = new MessagePack();
        byte[] bytes = messagePack.write(msg);
        out.add(bytes);
    }
}
