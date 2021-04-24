package org.maple.bytebuffer;


import java.nio.ByteBuffer;

/**
 * 测试 ByteBufferUtil 工具类
 */
public class ByteBufferDemo03 {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("abc".getBytes());
        ByteBufferUtil.debugAll(buffer);

        buffer.flip();
        buffer.get();
        ByteBufferUtil.debugAll(buffer);

    }
}
