package ch3.demo01;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Log4j2
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String body = new String(bytes, StandardCharsets.UTF_8);
        log.debug("The Time server receive order: {}", body);
        String currentTime = "QUERY TIME".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD QUERY";
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        //不是写到channel，而是写到buffer，通过下面的flush真正写到SocketChannel
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
