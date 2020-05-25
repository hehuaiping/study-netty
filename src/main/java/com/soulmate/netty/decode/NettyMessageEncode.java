package com.soulmate.netty.decode;

import com.soulmate.netty.common.MarshallingConst;
import com.soulmate.netty.message.Header;
import com.soulmate.netty.message.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Netty消息编码器
 * @author soulmate
 */
public class NettyMessageEncode extends MessageToMessageEncoder<NettyMessage> {

    /**
     * 解码器
     */
    private MarshallingEncoder marshallingEncoder;

    public NettyMessageEncode() {
        // 首先通过Marshalling工具类的精通方法获取Marshalling实例对象 参数serial标识创建的是java序列化工厂对象。
        MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory(MarshallingConst.SERIAL);
        // 创建了MarshallingConfiguration对象，配置了版本号为5
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(4);
        // 根据marshallerFactory和configuration创建provider
        DefaultMarshallerProvider provider = new DefaultMarshallerProvider(factory, configuration);
        // //构建Netty的MarshallingEncoder对象，MarshallingEncoder用于实现序列化接口的POJO对象序列化为二进制数组
        this.marshallingEncoder = new MarshallingEncoder(provider);
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

            }
        }
    }
}
