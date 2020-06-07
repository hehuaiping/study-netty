package com.soulmate.netty.client;

import com.soulmate.netty.decode.NettyMessageDecoder;
import com.soulmate.netty.encode.NettyMessageEncoder;
import com.soulmate.netty.hendler.auth.LoginAuthReqHandler;
import com.soulmate.netty.hendler.heartbeat.HeartBeatReqHandler;
import com.soulmate.netty.protocol.NettyConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author soulmate
 * netty客户端
 */
public class NettyClient {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    EventLoopGroup group = new NioEventLoopGroup();

    private void connect(String host, int port) throws InterruptedException {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new NettyClientInitializer());


            // 发起异步连接
           /* ChannelFuture future = bootstrap.connect(
                    new InetSocketAddress(host, port),
                    new InetSocketAddress(NettyConstant.LOCAL_IP, NettyConstant.LOCAL_PORT)).sync();*/
            ChannelFuture future = bootstrap.connect(host,port);
            future.channel().closeFuture().sync();
        }finally {
            // 所有资源释放完成后，清空资源，再次发起重连
            executor.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    connect(NettyConstant.REMOTE_IP, NettyConstant.REMOTE_PORT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }


    }

    public static void main(String[] args) throws InterruptedException {
        new NettyClient().connect(NettyConstant.REMOTE_IP, NettyConstant.REMOTE_PORT);
    }

}

class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 解码器
        pipeline.addLast(new NettyMessageDecoder(1024 << 10,4,4));
        // 编码器
        pipeline.addLast("MessageEncoder", new NettyMessageEncoder());
        // 心跳处理器  如果50s没有收到任何消息，则代表当前链路已断开，清理资源后重新连接
        pipeline.addLast("ReadTimeoutHandler", new ReadTimeoutHandler(50));
        // 握手请求处理器
        pipeline.addLast("LoginAuthReqHandler", new LoginAuthReqHandler());
        // 定时心跳请求处理器
        pipeline.addLast("HeartBeatHandler", new HeartBeatReqHandler());
    }
}
