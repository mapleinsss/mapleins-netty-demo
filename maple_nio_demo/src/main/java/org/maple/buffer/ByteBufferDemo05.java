package org.maple.buffer;

import lombok.extern.log4j.Log4j2;
import org.maple.util.ByteBufferUtil;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * ByteBuffer 读取和写入方法
 *
 * put/get/get(index)/rewind/mark/reset
 */
@Log4j2
public class ByteBufferDemo05 {
    public static void main(String[] args) {
        try (FileChannel channel = new FileInputStream("maple_nio_demo/data.txt").getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(16);

            // 1.调用 put 直接写入 buffer
            buffer.put("a".getBytes());

            // 2.调用 channel 的 read 方法
            channel.read(buffer);
            ByteBufferUtil.debugAll(buffer);

            buffer.flip();
            // 1. 调用 get 读取一个字节
            log.debug((char) buffer.get());

            // get(index) 方法不会移动 position 的指针
            log.debug((char) buffer.get(5));

            // rewind() 从头开始读
            buffer.rewind();
            log.debug((char) buffer.get());

            // mark 标记当前位置，reset 后，读指针会到 mark 的位置
            buffer.mark();
            buffer.get();
            buffer.get();
            buffer.reset();
            log.debug((char) buffer.get());

            // 2. 调用 channel 的 write 方法
            // channel.write(buffer);

            ByteBufferUtil.debugAll(buffer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
