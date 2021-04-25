package org.maple.ch4.demo02_LineBasedFrameDecoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j2;

import java.util.Date;

/**
 * LineBasedFrameDecoder 解码器会遍历 ByteBuf 字节，通过 /n 或 /r/n 作为结束位置，进行解码
 * 支持单行最大字节，如果到达最大长度，都没有换行，这条数据会被忽略
 *
 * StringDecoder 是把 ByteBuf 转 String 的解码器
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
                            channel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            channel.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                                private int counter;

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    String body = (String) msg;
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
