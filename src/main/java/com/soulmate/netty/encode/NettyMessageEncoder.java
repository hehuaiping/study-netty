package com.soulmate.netty.encode;

import com.soulmate.netty.common.MarshallingConst;
import com.soulmate.netty.message.Header;
import com.soulmate.netty.message.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Netty消息编码器
 * @author soulmate
 */
public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    /**
     * 编码
     */
    private final MarshallingEncoder marshallingEncoder;

    public NettyMessageEncoder(MarshallingEncoder marshallingEncoder) {
        this.marshallingEncoder = marshallingEncoder;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage msg, List<Object> out) throws Exception {
        if(Objects.isNull(msg) || Objects.isNull(msg.getHeader())) {
            throw new Exception("The encode message is null;");
        }
        Header header = msg.getHeader();
        // 创建不池化的buffer缓存区
        ByteBuf sendBuf = Unpooled.buffer();
        // 写入32bit crcCode
        sendBuf.writeInt(header.getCrcCode());
        // 写入32bit length
        sendBuf.writeInt(header.getLength());
        // 写入64bit sessionID
        sendBuf.writeLong(header.getSessionID());
        // 写入8bit type
        sendBuf.writeByte(header.getType());
        // 写入8bit priority
        sendBuf.writeByte(header.getPriority());
        // 写入32bit attachment size
        sendBuf.writeInt(header.getAttachment().size());
        // 写入附件
        if(Objects.nonNull(header.getAttachment())) {
            String key = null;
            byte[] keyArray = null;
            Object value = null;
            for(Map.Entry<String, Object> entries : header.getAttachment().entrySet()) {
                key = entries.getKey();
                keyArray = key.getBytes(MarshallingConst.CHART_SET);
                // key的长度
                sendBuf.writeInt(keyArray.length);
                // key字节数据
                sendBuf.writeBytes(keyArray);
                value = entries.getValue();
                // 对象编码
                marshallingEncoder.encode(value, sendBuf);
            }
            key = null;
            keyArray = null;
            value = null;
        }
        // 消息体
        if(Objects.nonNull(msg.getBody())) {
            // 对象编码
            marshallingEncoder.encode(msg.getBody(), sendBuf);
        }else {
            // 没有body
            sendBuf.writeInt(0);
            // 重新设置header中的消息长度
            sendBuf.setInt(4, sendBuf.readableBytes());
        }
    }
}
