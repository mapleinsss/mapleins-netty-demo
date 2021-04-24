package org.maple.bytebuffer;

import lombok.extern.log4j.Log4j2;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * bytebuffer 基本测试
 * Creates a new buffer with the given mark, position, limit, capacity
 * 可以看到有四个指针
 * 在写模式下：
 *      position：写入位置
 *      limit：写入限制
 *      capacity：bytebuffer 的容量
 *      mark：标记一个位置，用来回溯
 * 在读模式下：
 *      position：回到未读取字节的位置
 *      limit：能读取最大的字节数
 *
 * clear() 方法
 */
@Log4j2
public class ByteBufferDemo01 {
    public static void main(String[] args) {
        try (FileChannel channel = new FileInputStream("maple_nio_demo/data.txt").getChannel()) {
            // 缓冲区 ByteBuffer，只能读 5 个字节
            ByteBuffer buffer = ByteBuffer.allocate(5);
            while (true) {
                // 读取数据，写入 buffer
                int read = channel.read(buffer);
                log.debug("写入到 bytebuffer 字节为 {}", read);
                log.debug("position {}", buffer.position());
                log.debug("limit {}", buffer.limit());
                log.debug("capacity {}", buffer.capacity());

                if (read != -1) {
                    // 切换为读模式
                    buffer.flip();
                    log.debug("读取到 bytebuffer 字节为 {}", read);
                    log.debug("position {}", buffer.position());
                    log.debug("limit {}", buffer.limit());
                    log.debug("capacity {}", buffer.capacity());
                    while (buffer.hasRemaining()) {
                        byte b = buffer.get();
                        log.debug((char) b);
                    }
                } else {
                    break;
                }
                // 切换为写模式
//                buffer.flip();
                buffer.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
