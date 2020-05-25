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
     * 默认长度
     */
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    Marshaller marshaller;

    public MarshallingEncoder() throws IOException {
        marshaller = MarshallingCodecFactory.buildMarshalling();
    }

    protected void encode(Object msg, ByteBuf out) {

    }
}
