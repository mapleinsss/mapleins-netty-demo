package org.maple.ch10.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ShortToByteEncoder extends MessageToByteEncoder<Short> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Short aShort, ByteBuf byteBuf) throws Exception {
        byteBuf.writeByte(aShort);
    }
}
