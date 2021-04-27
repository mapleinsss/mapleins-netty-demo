package org.maple.ch12.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;
import org.maple.ch12.protocol.Header;
import org.maple.ch12.protocol.MessageType;
import org.maple.ch12.protocol.NettyMessage;

@Log4j2
public class SelfMsgSendHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
//        header.setSessionID(Long.parseLong(ctx.channel().id().asLongText()));
        header.setType(MessageType.BUSINESS_REQ.value());
        header.setPriority((byte) 1);
        String msg = "this is my msg";
        nettyMessage.setHeader(header);
        nettyMessage.setBody(msg);
        log.debug("生成的消息为：{}", nettyMessage);
        ctx.writeAndFlush(nettyMessage);
    }

}
