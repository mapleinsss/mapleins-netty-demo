package org.maple.bytebuffer;

import lombok.extern.log4j.Log4j2;

import java.nio.ByteBuffer;

/**
 * 粘包/半包
 */
@Log4j2
public class Test {

    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\nI'm Allen\nHo".getBytes());
        split(source);
        source.put("w are you?\n18!\n".getBytes());
        split(source);
    }

    private static void split(ByteBuffer source) {
        source.flip();
        int oldLimit = source.limit();
        for (int i = 0; i < oldLimit; i++) {
            if (source.get(i) == '\n') {
                log.debug(i);
                ByteBuffer target = ByteBuffer.allocate(i + 1 - source.position());
                // 0 ~ limit
                source.limit(i + 1);
                target.put(source);
                ByteBufferUtil.debugAll(target);
                source.limit(oldLimit);
            }
        }
        source.compact();
    }
}
