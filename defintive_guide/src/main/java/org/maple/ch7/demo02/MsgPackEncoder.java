package org.maple.ch7.demo02;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * 将 object 对象转换为 msgpack 装入 ByteBuf
 */
public class MsgPackEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        MessagePack messagePack = new MessagePack();
        byte[] bytes = messagePack.write(o);
        byteBuf.writeBytes(bytes);
    }
}
