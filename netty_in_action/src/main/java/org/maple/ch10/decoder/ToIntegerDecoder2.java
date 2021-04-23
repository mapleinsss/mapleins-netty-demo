package org.maple.ch10.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * ReplayingDecoder<S> 继承了 ByteToMessageDecoder
 * S 是状态管理，Void 表示不需要状态管理
 *
 * 不在使用 ByteBuf 而是替换成 ReplayingDecoderByteBuf
 * 也是做了检测，如果大于范围抛出一个 Signal
 * 使用的时候要注意 ReplayingDecoderByteBuf 中是否方法是否支持。
 *
 * ReplayingDecoder 速度慢于 ByteToMessageDecoder
 *
 */
public class ToIntegerDecoder2 extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        list.add(byteBuf);
    }
}
