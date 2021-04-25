package org.maple.buffer;

import lombok.extern.log4j.Log4j2;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Bytebuffer.get()：获取一个字节数据
 * Bytebuffer.hasRemaining()：判断是否还有没有读的数据
 * Bytebuffer.flip()：切换读写模式
 * Bytebuffer.clear()：清除掉 bytebuffer 中所有的数据
 * Bytebuffer.compact()：剔除掉已经读过的数据
 */
@Log4j2
public class ByteBufferDemo02 {

    public static void main(String[] args) {
        try (FileChannel channel = new FileInputStream("maple_nio_demo/data.txt").getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(20);
            // 读取数据，写入 buffer
            int read = channel.read(buffer);
            log.debug("写入到 bytebuffer 字节为 {}", read);
            log.debug("position {}", buffer.position());
            log.debug("limit {}", buffer.limit());
            log.debug("capacity {}", buffer.capacity());
            log.debug("-----------------------------------");

            // 切换读模式
            buffer.flip();
            // 获取一个字节
            buffer.get();
            log.debug("position {}", buffer.position());
            log.debug("limit {}", buffer.limit());
            log.debug("capacity {}", buffer.capacity());
            log.debug("-----------------------------------");

            // 切换为写模式,使用 compact() 方法
//            buffer.clear();
            buffer.compact();
            log.debug("position {}", buffer.position());
            log.debug("limit {}", buffer.limit());
            log.debug("capacity {}", buffer.capacity());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
