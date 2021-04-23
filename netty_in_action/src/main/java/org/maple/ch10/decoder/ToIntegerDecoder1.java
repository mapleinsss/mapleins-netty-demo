package org.maple.ch10.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 解码 int，占 4 个字节，当可读字节大于 4 时，读取装入 List，如果 List 不为空，传递给下一个 Handler
 */
public class ToIntegerDecoder1 extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() >= 4) {
            list.add(byteBuf.readInt());
        }
    }
}
