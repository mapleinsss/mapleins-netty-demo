package org.maple.ch12.cs;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.log4j.Log4j2;
import org.maple.ch12.coder.NettyMessageDecoder;
import org.maple.ch12.coder.NettyMessageEncoder;
import org.maple.ch12.handler.HeartBeatReqHandler;
import org.maple.ch12.handler.LoginAuthReqHandler;
import org.maple.ch12.handler.SelfMsgSendHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
public class NettyClient {

    public static void main(String[] args) throws Exception {
        new NettyClient().connect("localhost", 8888);
    }

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);

    public void connect(String host, int port) throws Exception {
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
                            channel.pipeline().addLast(new ReadTimeoutHandler(50));
                            channel.pipeline().addLast(new LoginAuthReqHandler());
                            channel.pipeline().addLast(new HeartBeatReqHandler());
                            channel.pipeline().addLast(new SelfMsgSendHandler());
//                            socketChannel.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
                        }
                    })
                    .connect(host, port)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } finally {
//            group.shutdownGracefully();
            // 重连操作
            EXECUTOR.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    try {
                        connect(host, port);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
