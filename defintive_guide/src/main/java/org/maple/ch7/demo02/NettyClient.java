package org.maple.ch7.demo02;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
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

                            socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder
                                    (65535, 0, 2 ,0 ,2));
                            socketChannel.pipeline().addLast(new MsgPackDecoder());
                            socketChannel.pipeline().addLast(new LengthFieldPrepender(2));
                            socketChannel.pipeline().addLast(new MsgPackEncoder());
                            socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                                private int counter;

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    UserInfo loopUser;
                                    for (int i = 1; i <= 100; i++) {
                                        loopUser = new UserInfo();
                                        loopUser.setName("Allen");
                                        loopUser.setAge(i);
                                        ctx.writeAndFlush(loopUser);
                                    }
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    log.debug("received msg is {} , counter is : {}", msg, ++counter);
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
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
