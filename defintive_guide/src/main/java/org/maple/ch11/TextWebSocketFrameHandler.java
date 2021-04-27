package org.maple.ch11;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

/**
 * 处理文本协议数据，处理 TextWebSocketFrame 类型的数据
 * websocket 专门处理文本的 frame 就是 TextWebSocketFrame
 */
@Log4j2
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.debug("收到消息：{}", msg.text());
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务时间：" + LocalDateTime.now()));
    }

    //每个 channel 都有一个唯一的 id 值
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 打印出 channel 唯一值，asLongText 方法是 channel 的 id 的全名
        log.debug("current added channel id is {}", ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug("current removed channel id is {}", ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}