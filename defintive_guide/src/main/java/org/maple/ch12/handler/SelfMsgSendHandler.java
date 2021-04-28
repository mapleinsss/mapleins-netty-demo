package org.maple.ch12.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;
import org.maple.ch12.protocol.Header;
import org.maple.ch12.protocol.MessageType;
import org.maple.ch12.protocol.NettyMessage;

import java.util.concurrent.TimeUnit;

@Log4j2
public class SelfMsgSendHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        NettyMessage nettyMessage = new NettyMessage();
//        Header header = new Header();
//        header.setSessionID(Long.parseLong(ctx.channel().id().asLongText()));
//        header.setType(MessageType.BUSINESS_REQ.value());
//        header.setPriority((byte) 1);
//        String msg = "this is my msg";
//        nettyMessage.setHeader(header);
//        nettyMessage.setBody(msg);
//        ctx.writeAndFlush(nettyMessage);
        log.debug("channel active");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 登录成功后，发送一条业务消息
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            NettyMessage nettyMessage = new NettyMessage();
            Header header = new Header();
//            header.setSessionID(Long.parseLong(ctx.channel().id().asLongText()));
            header.setType(MessageType.BUSINESS_REQ.value());
            header.setPriority((byte) 1);
            String m = "this is my msg";
            nettyMessage.setHeader(header);
            nettyMessage.setBody(m);
            ctx.writeAndFlush(nettyMessage);
        }
    }
}
