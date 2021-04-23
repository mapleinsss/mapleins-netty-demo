package org.maple.ch10.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CharToByteEncoder extends MessageToByteEncoder<Character> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Character aChar, ByteBuf byteBuf) throws Exception {
        byteBuf.writeChar(aChar);
    }
}
