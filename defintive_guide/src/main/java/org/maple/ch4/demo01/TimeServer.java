package org.maple.ch4.demo01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 *
 * 粘包和半包定义如下：
 *  粘包和半包，指的都不是一次是正常的 ByteBuf 缓存区接收。
 *      - 粘包，就是接收端读取的时候，多个发送过来的 ByteBuf “粘”在了一起。
 *        换句话说，接收端读取一次的 ByteBuf ，读到了多个发送端的 ByteBuf ，是为粘包。
 *      - 半包，就是接收端将一个发送端的ByteBuf “拆”开了，形成一个破碎的包，我们定义这种 ByteBuf 为半包。
 *        换句话说，接收端读取一次的 ByteBuf ，读到了发送端的一个 ByteBuf 的一部分，是为半包。
 *
 * TCP 的接收窗口会随带宽而变化，缓冲后的数据，向应用层提交的时候，TCP 是不管你一条数据格式是啥样的，把自己缓冲区足够的数据向上提交
 * 保证可靠传输即可。所以应用层在接收后塞入 Buffer 中可能就不是自己想要的一条数据。
 *
 * 演示粘包案例：服务端一次把所有数据收到，最后返回 bad query， count = 1
 *
 * 解决办法：
 *  - 消息定长：固定长度为 200 字节，不够用空格补齐
 *  - 在包尾增加换行符
 *  - 将消息分为消息头和消息体：头中存储消息体的总长度
 *  - 更复杂的协议
 */
@Log4j2
public class TimeServer {

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //backlog是阻塞队列
            new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                                private int counter;

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf buf = (ByteBuf) msg;
                                    byte[] bytes = new byte[buf.readableBytes()];
                                    buf.readBytes(bytes);
                                    // 改造此处
                                    String body = new String(bytes, StandardCharsets.UTF_8)
                                            .substring(0, bytes.length - System.getProperty("line.separator").length());
                                    log.debug("The Time server receive order: {}, the counter is : {}", body, ++counter);
                                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD QUERY";
                                    currentTime = currentTime + System.getProperty("line.separator");
                                    ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
                                    ctx.write(resp);
                                }

                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                    ctx.flush();
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    ctx.close();
                                }
                            });
                        }
                    })
                    .bind(8888)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅退出
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
