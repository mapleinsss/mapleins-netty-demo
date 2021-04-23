package org.maple.ch11.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

public class HttpServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            new ServerBootstrap()
                    .group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(8888))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // 添加 SSL/TSL，这里没实现，需要再做
//                            pipeline.addFirst(new SslHandler(sslEngine));
                            // HTTP 消息的编解码器，组合了 HttpRequestDecoder & HttpResponseEncoder
                            pipeline.addLast(new HttpServerCodec());
                            // 消息聚合器，将 HTTP 请求和响应聚合起来称为一个完整的消息
                            pipeline.addLast(new HttpObjectAggregator(512*1024));
                            // 开启 GZIP 压缩
                            pipeline.addLast(new HttpContentCompressor());
                            // 自定义处理 handler
                            pipeline.addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
                                    if (HttpUtil.is100ContinueExpected(req)) {
                                        ctx.write(new DefaultFullHttpResponse(
                                                HttpVersion.HTTP_1_1,
                                                HttpResponseStatus.CONTINUE));
                                    }
                                    // 获取请求的uri
                                    String uri = req.uri();
                                    String msg = "<html><head><title>测试页面</title></head><body>你请求uri为：" + uri+"</body></html>";
                                    // 创建http响应
                                    FullHttpResponse response = new DefaultFullHttpResponse(
                                            HttpVersion.HTTP_1_1,
                                            HttpResponseStatus.OK,
                                            Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
                                    // 设置头信息
                                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
                                    ctx.writeAndFlush(response);
                                    // 响应后手动关闭，否则浏览器不会关闭连接
                                    ctx.close();
                                }
                            });
                        }
                    })
                    .bind()
                    .sync()
                    .channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }

    }
}
