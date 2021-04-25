package org.maple.channel;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 对于 FileChannel，只能工作在阻塞的模式下
 * 不能直接打开 FileChannel，必须通过 FileInputStream、FileOutputStream 或者 RandomAccessFile
 * 来获取 FileChannel，它们都有 getChannel 方法
 * <p>
 * 通过 FileInputStream 获取的 channel 只能读
 * 通过 FileOutputStream 获取的 channel 只能写
 * 通过 RandomAccessFile 是否能读写根据构造 RandomAccessFile 时的读写模式（mode）决定
 */
@Log4j2
public class ChannelDemo01 {

    public static void main(String[] args) {
        try (FileChannel channel = new RandomAccessFile("maple_nio_demo/data.txt", "rw").getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(2);
            // read 返回读取的长度，没了返回 -1
            int readLength = channel.read(buffer);
            log.debug("读取到的长度为：{}", readLength);
            log.debug("channel 当前的 position：{}", channel.position());

            // 设置当前的 position，如果设置为文件的末尾，读取返回 -1，写入会写入黑洞
            channel.position(2);
            log.debug("channel 当前的 position：{}", channel.position());

            // 获取文件的大小
            log.debug("channel 文件的大小 size 为：{}", channel.size());
            // 操作系统出于性能的考虑，会将数据缓存，不是立刻写入磁盘。可以调用 force(true)  方法将文件内容和元数据（文件的权限等信息）立刻写入磁盘
//            channel.force(true);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
