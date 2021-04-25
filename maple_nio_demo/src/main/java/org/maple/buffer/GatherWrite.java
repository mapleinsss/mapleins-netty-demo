package org.maple.buffer;

import org.maple.util.ByteBufferUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 聚集写入
 */
public class GatherWrite {
    public static void main(String[] args) {
        try (RandomAccessFile file = new RandomAccessFile("maple_nio_demo/3parts.txt", "rw")) {
            FileChannel channel = file.getChannel();
            ByteBuffer d = ByteBuffer.allocate(4);
            ByteBuffer e = ByteBuffer.allocate(4);
//            channel.position(11);

            d.put(new byte[]{'f', 'o', 'u', 'r'});
            e.put(new byte[]{'f', 'i', 'v', 'e'});
            d.flip();
            e.flip();
            ByteBufferUtil.debugAll(d);
            ByteBufferUtil.debugAll(e);
            channel.write(new ByteBuffer[]{d, e});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
