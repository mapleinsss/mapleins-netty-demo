package org.maple.ch5.demo02_FixedLengthFrameDecoder;

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
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.log4j.Log4j2;

/**
 * 定长解码器 FixedLengthFrameDecoder，长度到了就解码
 */
@Log4j2
public class NettyServer {
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

                            // 指定最大长度
                            channel.pipeline().addLast(new FixedLengthFrameDecoder(12));
                            channel.pipeline().addLast(new StringDecoder());
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                                private int counter;

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    String body = (String) msg;
                                    log.debug("received msg is {} , counter is : {}", body, ++counter);
                                    ByteBuf resp = Unpooled.copiedBuffer(body.getBytes());
                                    ctx.write(resp);
                                }

                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                    ctx.flush();
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
