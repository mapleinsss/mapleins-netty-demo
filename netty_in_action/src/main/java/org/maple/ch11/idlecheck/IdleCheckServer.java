package org.maple.ch11.idlecheck;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * 通过 IdleStateHandler 实现空闲检测
 */
@Log4j2
public class IdleCheckServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            new ServerBootstrap()
                    .group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(8888))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LoggingHandler());
                            /**
                             * IdleStateHandler 将在被触发时发送一个 IdleStateEvent 事件
                             * 读空闲时间、写空闲时间、读写空闲时间、时间单位
                             */
                            socketChannel.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                            socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    // 如果 5 秒没发生读事件，打印
                                    if (evt instanceof IdleStateEvent) {
                                        // 触发了读空闲事件
                                        if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                                            log.debug("已经 5 秒没有收到读事件了");
                                        }
                                    } else {
                                        super.userEventTriggered(ctx, evt);
                                    }
                                }
                            });
                        }
                    })
                    .bind()
                    .sync()
                    .channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }

    }
}
