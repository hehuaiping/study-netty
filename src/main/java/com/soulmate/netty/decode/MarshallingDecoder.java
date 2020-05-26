package com.soulmate.netty.decode;

import com.soulmate.netty.factory.MarshallingCodecFactory;
import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

/**
 * @author soulmate
 * 解码器
 */
public class MarshallingDecoder {
    /**
     * 解码器
     */
    private final Unmarshaller unmarshaller;

    public MarshallingDecoder() throws IOException {
        unmarshaller = MarshallingCodecFactory.buildUnMarshalling();
    }

    protected Object decode(ByteBuf in) throws IOException, ClassNotFoundException {
        // 读取对象码流长度字段
        int objSize = in.readInt();
        // 从读取索引处开始读取对象码流
        ByteBuf buf = in.slice(in.readableBytes(), objSize);
        ChannelBufferByteInput input = new ChannelBufferByteInput(buf);
        Object object = null;
        try {
            // 开始读取码流
            unmarshaller.start(input);
            object = unmarshaller.readObject();
            unmarshaller.finish();
        }finally {
            unmarshaller.close();
        }
        return object;
    }
}
