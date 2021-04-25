package org.maple.ch5.demo01_DelimiterBasedFrameDecoder;

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
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j2;

import java.util.Date;

/**
 * DelimiterBasedFrameDecoder 解码器会遍历 ByteBuf 字节，通过自定义分隔符作为结束位置，进行解码
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
                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes(CharsetUtil.UTF_8));
                            // 指定最大长度和分隔符
                            channel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            channel.pipeline().addLast(new StringDecoder());
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                                private int counter;

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    String body = (String) msg;
                                    log.debug("The Time server receive order: {}, the counter is : {}", body, ++counter);
                                    body = body.replace("$_", "");
                                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD QUERY";
                                    currentTime = currentTime + "$_";
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
