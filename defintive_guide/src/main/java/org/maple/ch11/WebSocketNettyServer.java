package org.maple.ch11;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.log4j.Log4j2;
import org.maple.ch9.marshalling_netty.MarshallingCodeCFactory;

/**
 * websocket 服务器
 */
@Log4j2
public class WebSocketNettyServer {
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();

                            // websocket 协议本身是基于 http 协议的，所以这边也要使用 http 解编码器
                            pipeline.addLast(new HttpServerCodec());
                            // 以块的方式来写的处理器
                            pipeline.addLast(new ChunkedWriteHandler());
                            // netty 是基于分段请求的，HttpObjectAggregator 的作用是将请求分段再聚合,参数是聚合字节的最大长度
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            // ws://server:port/context_path
                            // ws://localhost:8888/ws
                            // 参数指的是 context_path
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                            // websocket 定义了传递数据的 6 种 frame 类型
                            pipeline.addLast(new TextWebSocketFrameHandler());
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
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
