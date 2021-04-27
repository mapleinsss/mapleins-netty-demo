package org.maple.ch12.cs;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.log4j.Log4j2;
import org.maple.ch12.coder.NettyMessageDecoder;
import org.maple.ch12.coder.NettyMessageEncoder;
import org.maple.ch12.handler.SelfMsgSendHandler;

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
                        protected void initChannel(SocketChannel channel) throws Exception {
                            // 长度字段放在 crcCode 后的 4 个字节
                            channel.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                            channel.pipeline().addLast(new NettyMessageEncoder());
                            channel.pipeline().addLast(new SelfMsgSendHandler());
//                            socketChannel.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
//                            socketChannel.pipeline().addLast("LoginAuthHandler", new LoginAuthReqHandler());
//                            socketChannel.pipeline().addLast("HeartBeatHandler", new HeartBeatReqHandler());
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
