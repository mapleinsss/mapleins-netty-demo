package org.maple.ch12.marshalling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import org.jboss.marshalling.Marshaller;

import java.io.IOException;

@ChannelHandler.Sharable
public class MarshallingEncoder {

    // 四个字节占位符，用来存储字符长度
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    Marshaller marshaller;

    public MarshallingEncoder() throws IOException {
        marshaller = MarshallingCodecFactory.buildMarshalling();
    }

    // 使用 marshall 对 Object 进行编码，并且写入 ByteBuf
    public void encode(Object msg, ByteBuf out) throws Exception {
        try {
            // 1. 获取写入位置
            int lengthPos = out.writerIndex();
            // 2. 先写入 4 个bytes，用于记录 Object 对象编码后长度
            out.writeBytes(LENGTH_PLACEHOLDER);
            // 3. 使用代理对象，防止 marshaller 写完之后关闭 ByteBuf
            ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
            // 4. 开始使用 marshaller 往 ByteBuf 中编码
            marshaller.start(output);
            marshaller.writeObject(msg);
            // 5. 结束编码
            marshaller.finish();
            // 6. 在起始位置，存入 4 个字节的对象长度
            // out.writerIndex() - lengthPos - 4 = 当前写指针的索引 - 开始写入的索引 - 4 个字节占位的长度 = 加密 msg 的长度
            out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
        } finally {
            marshaller.close();
        }
    }
}
