package org.maple.ch4.demo01;

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
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;

@Log4j2
public class TimeClient {
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
                            socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                                private int counter;

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    byte[] req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
                                    ByteBuf msg;
                                    // 发送了 100条 QUERY TIME ORDER\r\n
                                    for (int i = 0; i < 100; i++) {
                                        msg = Unpooled.buffer(req.length);
                                        msg.writeBytes(req);
                                        ctx.writeAndFlush(msg);
                                    }
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf buf = (ByteBuf) msg;
                                    byte[] bytes = new byte[buf.readableBytes()];
                                    buf.readBytes(bytes);
                                    String body = new String(bytes, StandardCharsets.UTF_8);
                                    log.debug("Now is : {}; the count is : {}", body, ++counter);
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    log.debug("Exception");
                                    cause.printStackTrace();
                                    ctx.close();
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
