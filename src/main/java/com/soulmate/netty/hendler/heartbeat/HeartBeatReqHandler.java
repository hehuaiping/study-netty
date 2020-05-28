package com.soulmate.netty.hendler.heartbeat;

import com.soulmate.netty.common.MessageType;
import com.soulmate.netty.message.Header;
import com.soulmate.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author soulmate
 * 心跳请求处理器
 */
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {
    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 握手成功之后，开始主动发送心跳
        if(Objects.nonNull(message.getHeader()) && message.getHeader().getType() == MessageType.LOGIN_RESP) {
            ctx.executor().scheduleAtFixedRate(() -> {
                NettyMessage heartBeat = buildHeartBeat();
                System.out.println("Client send heart beat message to server : ---> " + message);
                ctx.writeAndFlush(heartBeat);
            },0, 5000, TimeUnit.MILLISECONDS);
        }else if(Objects.nonNull(message.getHeader()) && message.getHeader().getType() == MessageType.HEART_RESP) {
            // 收到心跳响应
            System.out.println("Client receive server heart beat message : ---> " + message);
        }else {
            // 执行下一个处理器
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 构造心跳请求
     * @return
     */
    private NettyMessage buildHeartBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEART_REQ);
        message.setHeader(header);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if(Objects.nonNull(heartBeat)) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        // 执行下一个处理器
        ctx.fireExceptionCaught(cause);
    }
}
