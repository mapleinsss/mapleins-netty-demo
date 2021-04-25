package org.maple.selector;

import lombok.extern.log4j.Log4j2;
import org.maple.util.ByteBufferUtil;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 单线程可以配合 Selector 完成对多个 Channel 可读写事件的监控，这称之为多路复用:
 *      一个线程配合 selector 就可以监控多个 channel 的事件，事件发生线程才去处理。避免非阻塞模式下所做无用功
 *      让这个线程能够被充分利用
 *      节约了线程的数量
 *      减少了线程上下文切换
 *
 * 阻塞直到绑定事件发生
 * int count = selector.select();
 *
 * 阻塞直到绑定事件发生，或是超时（时间单位为 ms）
 * int count = selector.select(long timeout);
 *
 * 不会阻塞，也就是不管有没有事件，立刻返回，自己根据返回值检查是否有事件
 * int count = selector.selectNow();
 *
 * selector 何时不阻塞：
 *  - 事件发生时
 *       客户端发起连接请求，会触发 accept 事件
 *       客户端发送数据过来，客户端正常、异常关闭时，都会触发 read 事件，另外如果发送的数据大于 buffer 缓冲区，会触发多次读取事件
 *       channel 可写，会触发 write 事件
 *       在 linux 下 nio bug 发生时
 *  - 调用 selector.wakeup()
 *  - 调用 selector.close()
 *  - selector 所在线程 interrupt
 *
 * 事件发生后，要么处理，要么取消（cancel），不能什么都不做，否则下次该事件仍会触发
 */
@Log4j2
public class SelectorDemo01 {
    public static void main(String[] args) {
        try (ServerSocketChannel channel = ServerSocketChannel.open()) {
            channel.bind(new InetSocketAddress(8080));
            channel.configureBlocking(false);

            /**
             * 注册事件，绑定的事件 selector 才会关心
             * * channel 必须工作在非阻塞模式
             * * 绑定的事件类型可以有
             *   * connect - 客户端连接成功时触发
             *   * accept - 服务器端成功接受连接时触发
             *   * read - 数据可读入时触发，有因为接收能力弱，数据暂不能读入的情况
             *   * write - 数据可写出时触发，有因为发送能力弱，数据暂不能写出的情况
             */
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                log.debug("-----");
                // 阻塞
                int count = selector.select();
                // 不阻塞
//                int count = selector.selectNow();
                log.debug("select count: {}", count);
//                if(count <= 0) {
//                    continue;
//                }
                // 获取所有事件
                Set<SelectionKey> keys = selector.selectedKeys();

                // 遍历所有事件，逐一处理
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    // 判断事件类型
                    if (key.isAcceptable()) {
                        ServerSocketChannel c = (ServerSocketChannel) key.channel();
                        // 必须处理
                        SocketChannel sc = c.accept();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);
                        log.debug("连接已建立: {}", sc);
                    } else if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(128);
                        int read = sc.read(buffer);
                        if(read == -1) {
                            key.cancel();
                            sc.close();
                        } else {
                            buffer.flip();
                            ByteBufferUtil.debugAll(buffer);
                        }
                    }
                    // 处理完毕，必须将事件移除
                    iter.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
