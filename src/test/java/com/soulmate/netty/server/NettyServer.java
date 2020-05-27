package com.soulmate.netty.server;

import com.soulmate.netty.decode.NettyMessageDecoder;
import com.soulmate.netty.encode.NettyMessageEncoder;
import com.soulmate.netty.hendler.auth.LoginAuthRespHandler;
import com.soulmate.netty.hendler.heartbeat.HeartBeatRespHandler;
import com.soulmate.netty.protocol.NettyConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @author soulmate
 */
public class NettyServer {
    public void bind() throws InterruptedException {
        // 用来接收请求
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用户处理请求
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new NettyServerInitializer());

        // 绑定端口，同步等待连接
        ChannelFuture future = serverBootstrap.bind(NettyConstant.REMOTE_IP, NettyConstant.REMOTE_PORT).sync();

        System.out.println("Netty server start ok : " + NettyConstant.REMOTE_IP + " : " + NettyConstant.REMOTE_PORT);
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyServer().bind();
    }
}

class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 解码器
        pipeline.addLast(new NettyMessageDecoder(1024 << 1,4,4));
        // 解码器
        pipeline.addLast("NettyMessageEncoder", new NettyMessageEncoder());
        // 解码器
        pipeline.addLast("ReadTimeoutHandler", new ReadTimeoutHandler(50));
        // 登录校验处理器
        pipeline.addLast("LoginAuthRespHandler", new LoginAuthRespHandler());
        // 心跳处理器
        pipeline.addLast("HeartBeatRespHandler", new HeartBeatRespHandler());
    }
}
