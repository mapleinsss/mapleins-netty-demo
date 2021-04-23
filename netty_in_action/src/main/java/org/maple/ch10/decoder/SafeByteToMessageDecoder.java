package org.maple.ch10.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * 由于Netty 是一个异步框架，所以需要在字节可以解码之前在内存中缓冲它们。因此，不能
 * 让解码器缓冲大量的数据以至于耗尽可用的内存。为了解除这个常见的顾虑，Netty 提供了
 * TooLongFrameException 类，其将由解码器在帧超出指定的大小限制时抛出。
 */
public class SafeByteToMessageDecoder extends ByteToMessageDecoder {

    private static final int MAX_FRAME_SIZE = 1024;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int readable = byteBuf.readableBytes();
        if (readable > MAX_FRAME_SIZE) {
            // 直接扔掉这个帧
            byteBuf.skipBytes(readable);
            throw new TooLongFrameException("Frame is Bigger than deal");
        }
    }
}
