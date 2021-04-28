package org.maple.ch12.cs;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.log4j.Log4j2;
import org.maple.ch12.coder.NettyMessageDecoder;
import org.maple.ch12.coder.NettyMessageEncoder;
import org.maple.ch12.handler.HeartBeatRespHandler;
import org.maple.ch12.handler.LoginAuthRespHandler;
import org.maple.ch12.handler.SelfMsgReceiveHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
public class NettyServer {

    public static void main(String[] args) throws Exception {
        new NettyServer().init(8888);
    }


    public void init(int port) throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                            channel.pipeline().addLast(new NettyMessageEncoder());
                            channel.pipeline().addLast(new ReadTimeoutHandler(50));
                            channel.pipeline().addLast(new LoginAuthRespHandler());
                            channel.pipeline().addLast(new HeartBeatRespHandler());
                            channel.pipeline().addLast(new SelfMsgReceiveHandler());
                        }
                    })
                    .bind(port)
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
//            优雅退出
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}
