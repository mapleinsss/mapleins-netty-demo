package org.maple.bytebuffer;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 分散读取
 */
@Log4j2
public class ScatterRead {

    public static void main(String[] args) {
        try (RandomAccessFile file = new RandomAccessFile("maple_nio_demo/data.txt", "rw")) {
            FileChannel channel = file.getChannel();
            ByteBuffer a = ByteBuffer.allocate(3);
            ByteBuffer b = ByteBuffer.allocate(2);
            ByteBuffer c = ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[]{a, b, c});
            a.flip();
            b.flip();
            c.flip();
            ByteBufferUtil.debugAll(a);
            ByteBufferUtil.debugAll(b);
            ByteBufferUtil.debugAll(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
