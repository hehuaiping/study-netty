package com.soulmate.netty.hendler.auth;

import com.soulmate.netty.common.MessageType;
import com.soulmate.netty.message.Header;
import com.soulmate.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Objects;

/**
 * @author soulmate
 * 登录请求处理器
 */
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 通道激活的时候，构建登录认证
        NettyMessage loginReq = buildLoginReq();
        System.out.println("Client send login request : ---> " + loginReq);
        ctx.writeAndFlush(loginReq);
    }

    private NettyMessage buildLoginReq() {
        NettyMessage msg = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ);
        msg.setHeader(header);
        return msg;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if(Objects.nonNull(message.getHeader()) && message.getHeader().getType() == MessageType.LOGIN_REQ) {
            if((byte)message.getBody() != 0) {
                // 握手失败，关闭连接
                ctx.close();
            }else {
                System.out.println("Login is ok :" + message);
                // 调用下一个注册的handler
                ctx.fireChannelRead(msg);
            }
        }else {
            // 调用下一个注册的handler
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
