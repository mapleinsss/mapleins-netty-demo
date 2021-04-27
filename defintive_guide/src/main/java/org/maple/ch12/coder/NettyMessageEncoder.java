package org.maple.ch12.coder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j2;
import org.maple.ch12.marshalling.MarshallingEncoder;
import org.maple.ch12.protocol.NettyMessage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Log4j2
public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    MarshallingEncoder marshallingEncoder;

    public NettyMessageEncoder() throws IOException {
        this.marshallingEncoder = new MarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage msg, List<Object> list) throws Exception {
        // 消息、头部不能为空
        if (null == msg || null == msg.getHeader()) {
            throw new Exception("The encode message is null");
        }
        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeInt(msg.getHeader().getCrcCode());
        // 假 length，占位用
        sendBuf.writeInt(msg.getHeader().getLength());
        sendBuf.writeLong(msg.getHeader().getSessionID());
        sendBuf.writeByte(msg.getHeader().getType());
        sendBuf.writeByte(msg.getHeader().getPriority());
        sendBuf.writeInt(msg.getHeader().getAttachment().size());

        for (Map.Entry<String, Object> param : msg.getHeader().getAttachment().entrySet()) {
            String key = param.getKey();
            byte[] keyArray = key.getBytes(CharsetUtil.UTF_8);
            sendBuf.writeInt(keyArray.length);
            // map key
            sendBuf.writeBytes(keyArray);
            Object value = param.getValue();
            // map value ，用 marshalling 加密
            marshallingEncoder.encode(value, sendBuf);
        }

        // body, 用 marshalling 加密
        if (msg.getBody() != null) {
            marshallingEncoder.encode(msg.getBody(), sendBuf);
        } else {
            // body 为空写入 4 个字节
            sendBuf.writeInt(0);
        }
        // 设置编码后的 length 长度
        // 在 crcCode(int) 后，占 4 个字节
        // 编码后设置 length（记录了除了 crcCode&length） = 可以读取的长度 - crcCode 4 个字节长度 - length 4 个字节的占位长度
        sendBuf.setInt(4, sendBuf.readableBytes() - 8);
        list.add(sendBuf);
    }
}
