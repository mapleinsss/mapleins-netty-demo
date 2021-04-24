package org.maple.bytebuffer;

import lombok.extern.log4j.Log4j2;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 字符串和 ByteBuffer 相互转换
 */
@Log4j2
public class ByteBufferDemo06 {
    public static void main(String[] args) {
        // 字符串转 ByteBuffer
        ByteBuffer buffer1 = ByteBuffer.allocate(16);
        buffer1.put("hello".getBytes(StandardCharsets.UTF_8));
        ByteBufferUtil.debugAll(buffer1);

        // StandardCharsets，分配字符串长度的空间，并且自动切换为读模式
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        // HeapByteBuffer
        log.debug(buffer2.getClass());
        // 5
        log.debug(buffer2.capacity());
        ByteBufferUtil.debugAll(buffer2);

        // wrap,分配字符串长度的空间，并且自动切换为读模式
        ByteBuffer buffer3 = ByteBuffer.wrap("hello".getBytes());
        // HeapByteBuffer
        log.debug(buffer2.getClass());
        // 5
        log.debug(buffer2.capacity());
        ByteBufferUtil.debugAll(buffer2);


        // 对于读模式的 buffer，StandardCharsets 将 ByteBuffer 转字符串
        log.debug(StandardCharsets.UTF_8.decode(buffer2).toString());

        // 对于写模式的 buffer 需要先切换为读模式
        buffer1.flip();
        log.debug(StandardCharsets.UTF_8.decode(buffer1).toString());
    }
}
