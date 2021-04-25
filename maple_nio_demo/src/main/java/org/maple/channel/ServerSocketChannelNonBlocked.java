package org.maple.channel;

import lombok.extern.log4j.Log4j2;
import org.maple.util.ByteBufferUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 单线程 开启异步方式：
 *  ssc.accept() 在没有连接时，返回 null，继续执行
 *  channel.read() 在没有读到数据返回 0，继续执行
 *
 *  缺陷：
 *  每次遍历都要判断是否有 accept 事件，判断 channel 是否有数据，造成 CPU 空转浪费
 *  假设有数据了，读取操作依旧是阻塞的
 */
@Log4j2
public class ServerSocketChannelNonBlocked {
    public static void main(String[] args) {
        //  单线程
        ByteBuffer buffer = ByteBuffer.allocate(16);
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            // 非阻塞模式
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(8080));
            List<SocketChannel> channels = new ArrayList<>();
            while (true) {
                // 非阻塞，线程还会继续运行，如果没有连接建立，但 sc 是 null
                SocketChannel sc = ssc.accept();
                if (sc != null) {
                    log.debug("connected... {}", sc);
                    // 非阻塞模式
                    sc.configureBlocking(false);
                    channels.add(sc);
                }
                for (SocketChannel channel : channels) {
                    // 非阻塞，线程仍然会继续运行，如果没有读到数据，read 返回 0
                    int read = channel.read(buffer);
                    if (read > 0) {
                        buffer.flip();
                        ByteBufferUtil.debugRead(buffer);
                        buffer.clear();
                        log.debug("after read...{}", channel);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
