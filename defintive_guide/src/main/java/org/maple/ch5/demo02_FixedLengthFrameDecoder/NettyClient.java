package org.maple.ch5.demo02_FixedLengthFrameDecoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            // 指定最大长度
                            socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(12));
                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                                private int counter;

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    byte[] req = "hello world!".getBytes();
                                    ByteBuf msg;
                                    for (int i = 0; i < 100; i++) {
                                        msg = Unpooled.buffer(req.length);
                                        msg.writeBytes(req);
                                        ctx.writeAndFlush(msg);
                                    }
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    log.debug("received msg is {} , counter is : {}", msg, ++counter);
                                }

                            });
                        }
                    })
                    .connect("localhost", 8888)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
