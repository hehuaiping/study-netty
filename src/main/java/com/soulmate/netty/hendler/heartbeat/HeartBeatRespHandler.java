package com.soulmate.netty.hendler.heartbeat;

import com.soulmate.netty.common.MessageType;
import com.soulmate.netty.message.Header;
import com.soulmate.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Objects;

/**
 * @author soulmate
 * 心跳响应处理器
 */
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 返回心跳应答消息
        if(Objects.nonNull(message.getHeader()) && message.getHeader().getType() == MessageType.HEART_REQ) {
            System.out.println("Receive client heart beat message : ---> " + message);
            // 构建心跳应答消息
            NettyMessage heartBeatRespMessage = buildHeartBeat();
            System.out.println("Send heart beat message to client : ---> " + heartBeatRespMessage);

            ctx.writeAndFlush(heartBeatRespMessage);
        }else {
            // 调用下一个处理器
            ctx.fireChannelRead(msg);
        }
    }

    private NettyMessage buildHeartBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEART_RESP);
        message.setHeader(header);
        return message;
    }
}
