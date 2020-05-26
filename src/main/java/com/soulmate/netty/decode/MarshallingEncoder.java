package com.soulmate.netty.decode;

import com.soulmate.netty.factory.MarshallingCodecFactory;
import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Marshaller;

import java.io.IOException;

/**
 * @author soulmate
 */
public class MarshallingEncoder {
    /**
     * 长度占位符
     */
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    Marshaller marshaller;

    public MarshallingEncoder() throws IOException {
        marshaller = MarshallingCodecFactory.buildMarshalling();
    }

    protected void encode(Object msg, ByteBuf out) throws IOException {
        try {
            // 已写入长度位置
            int lengthPos = out.writerIndex();
            // 先写入32bit占位符（我理解的是用32bit表示这个对象的码流长度，传输到接收端到时候，接收端用这个长度字段读取对象码流，
            // 然后再解码出对象
            out.writeBytes(LENGTH_PLACEHOLDER);
            ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
            marshaller.start(output);
            marshaller.writeObject(msg);
            // 28
            marshaller.finish();
            // 对象序列化完成后，根据writerIndex 计算对象码流长度，更新占位符数据
            // setInt方法不会改变writerIndex和readerIndex
            // 用当前已写入字节长度 - 占位符之前已写入字节长度 - 减去占位符长度 = 对象码流字节长度
            out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
        }finally {
            marshaller.close();
        }
    }
}
