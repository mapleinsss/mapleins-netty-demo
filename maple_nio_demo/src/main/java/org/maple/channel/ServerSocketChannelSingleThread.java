package org.maple.channel;

import lombok.extern.log4j.Log4j2;
import org.maple.util.ByteBufferUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 阻塞模式下，相关方法都会导致线程暂停
 * ServerSocketChannel.accept 会在没有连接建立时让线程暂停
 * SocketChannel.read 会在没有数据可读时让线程暂停
 * 阻塞的表现其实就是线程暂停了，暂停期间不会占用 cpu，但线程相当于闲置
 * 单线程下，阻塞方法之间相互影响，几乎不能正常工作，需要多线程支持
 * 但多线程下，有新的问题，体现在以下方面:
 *  32 位 jvm 一个线程 320k，64 位 jvm 一个线程 1024k，如果连接数过多，必然导致 OOM，并且线程太多，反而会因为频繁上下文切换导致性能降低
 *  可以采用线程池技术来减少线程数和线程上下文切换，但治标不治本，如果有很多连接建立，但长时间 inactive，会阻塞线程池中所有线程，因此不适合长连接，只适合短连接
 */
@Log4j2
public class ServerSocketChannelSingleThread {
    public static void main(String[] args) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            ssc.bind(new InetSocketAddress(8080));
            while (true) {
                log.debug("connecting...");
                // 阻塞方法，线程停止运行
                SocketChannel sc = ssc.accept();
                log.debug("connected... {}", sc);
                log.debug("before read... {}", sc);
                // 阻塞方法，线程停止运行，单线程模式下，其他连接无法被 accept()
                sc.read(buffer);
                buffer.flip();
                ByteBufferUtil.debugRead(buffer);
                buffer.clear();
                log.debug("after read...{}", sc);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
