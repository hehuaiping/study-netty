package com.soulmate.netty.hendler.auth;

import com.soulmate.netty.common.MessageType;
import com.soulmate.netty.message.Header;
import com.soulmate.netty.message.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author soulmate
 * 登录请求处理器
 */
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {
    /**
     * 可以用redis做缓存
     */
    private List<String> ipWhiteList = null;
    /**
     * 已连接的客户端 可以用redis缓存
     */
    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>(8);

    /**
     * 构造器会获取IP白名单
     */
    public LoginAuthRespHandler() {
        // 获取IP白名单
        ipWhiteList = new ArrayList<>(8);
        ipWhiteList.add("127.0.0.1");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 只处理握手请求消息，其他消息则透传
        if(Objects.nonNull(message.getHeader()) && message.getHeader().getType() == MessageType.LOGIN_REQ) {
            // 获取连接方IP地址
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;
            // 重复登录，拒绝
            if(nodeCheck.containsKey(nodeIndex)) {
                loginResp = buildResp((byte) -1);
            }else {
                // IP过滤
                InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
                String hostIp = socketAddress.getAddress().getHostAddress();
                // 是否通过IP认证
                boolean ok = false;
                if(ipWhiteList.contains(hostIp)) {
                    ok = true;
                    nodeCheck.put(nodeIndex, true);
                    loginResp = buildResp((byte) 0);
                }else {
                    // IP认证失败
                    loginResp= buildResp((byte )-1);
                }
                // 响应连接方
                System.out.println("the login response is : " + loginResp + "body [" + loginResp.getBody() + "]");
                ctx.writeAndFlush(loginResp);
            }
        }else {
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 构建握手响应
     * @param result
     * @return
     */
    private NettyMessage buildResp(byte result) {
        NettyMessage msg = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP);
        msg.setHeader(header);
        msg.setBody(result);
        return msg;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 删除缓存
        nodeCheck.remove(ctx.channel().remoteAddress());
        ctx.close();
        ctx.fireExceptionCaught(cause);
    }
}
