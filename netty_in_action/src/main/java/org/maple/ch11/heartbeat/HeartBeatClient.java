package org.maple.ch11.heartbeat;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j2;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Log4j2
public class HeartBeatClient {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(8888))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LoggingHandler());
                            socketChannel.pipeline().addLast(new IdleStateHandler(0, 6, 0, TimeUnit.SECONDS));
                            socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                                private final ByteBuf HEART_SEQUENCE =
                                        Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HEARTBEAT", CharsetUtil.UTF_8));

                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    // 如果客户端 3 秒没有写事件，自动发送心跳包
                                    if (evt instanceof IdleStateEvent) {
                                        // 触发了写空闲事件
                                        if (((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
                                            ctx.writeAndFlush(HEART_SEQUENCE.duplicate()).addListener(
                                                    ChannelFutureListener.CLOSE_ON_FAILURE
                                            );
                                        }
                                    } else {
                                        super.userEventTriggered(ctx, evt);
                                    }
                                }
                            });
                        }
                    })
                    .connect()
                    .sync()
                    .channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }

    }
}
