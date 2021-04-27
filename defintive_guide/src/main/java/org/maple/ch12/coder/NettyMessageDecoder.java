package org.maple.ch12.coder;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.CharsetUtil;
import org.maple.ch12.marshalling.MarshallingDecoder;
import org.maple.ch12.protocol.Header;
import org.maple.ch12.protocol.NettyMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    MarshallingDecoder marshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        marshallingDecoder = new MarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(frame.readInt());
        header.setLength(frame.readInt());
        header.setSessionID(frame.readLong());
        header.setType(frame.readByte());
        header.setPriority(frame.readByte());

        // 处理 map
        int size = frame.readInt();
        if (size > 0) {
            Map<String, Object> attachment = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                int keySize = frame.readInt();
                byte[] keyArray = new byte[keySize];
                frame.readBytes(keyArray);
                String key = new String(keyArray, CharsetUtil.UTF_8);
                attachment.put(key, marshallingDecoder.decode(frame));
            }
            header.setAttachment(attachment);
        }
        // 处理 body
        if (frame.readableBytes() > 4) {
            message.setBody(marshallingDecoder.decode(frame));
        }
        message.setHeader(header);
        return message;
    }

}
