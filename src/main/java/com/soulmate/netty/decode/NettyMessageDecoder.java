package com.soulmate.netty.decode;

import com.soulmate.netty.common.MarshallingConst;
import com.soulmate.netty.message.Header;
import com.soulmate.netty.message.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author soulmate
 * netty消息解码器
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private MarshallingDecoder marshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        marshallingDecoder = new MarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if(Objects.isNull(frame)) {
            System.out.println("解码帧数据为: " +  frame);
            return null;
        }
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        // 读取32bit
        header.setCrcCode(in.readInt());
        // 读取32bit
        header.setLength(in.readInt());
        // 读取64bit
        header.setSessionID(in.readLong());
        // 读取8bit
        header.setType(in.readByte());
        // 读取8bit
        header.setPriority(in.readByte());

        // 是否有附件
        int attachmentSize = in.readInt();
        if(attachmentSize > 0) {
            // 解码附件
            Map<String, Object> attach = new HashMap<>(attachmentSize);
            // key size
            int keySize;
            // key array
            byte[] keyArray;
            // key
            String key;

            for(int i = 0; i > attachmentSize; i ++) {
                // 读取key长度
                keySize = in.readInt();
                // 读取key
                keyArray = new byte[keySize];
                in.readBytes(keyArray);
                key = new String(keyArray, MarshallingConst.CHART_SET);
                attach.put(key, marshallingDecoder.decode(in));
            }
            keyArray = null;
            key = null;
            header.setAttachment(attach);
        }

        // 解码body
        if(in.readableBytes() > MarshallingConst.LENGTH_PLACEHOLDER) {
            message.setBody(marshallingDecoder.decode(in));
        }
        message.setHeader(header);
        // 解码完成
        return message;
    }


}
