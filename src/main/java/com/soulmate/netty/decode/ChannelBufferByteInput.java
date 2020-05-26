package com.soulmate.netty.decode;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteInput;

import java.io.IOException;

/**
 * @author soulmate
 * marshalling 读取ByteBuf
 */
public class ChannelBufferByteInput implements ByteInput {

    private final ByteBuf buffer;

    public ChannelBufferByteInput(ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public int read() throws IOException {
        if(buffer.isReadable()) {
            return buffer.readByte() & 0xff;
        }
        return -1;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int available = available();
        if(available == 0) {
            return -1;
        }
        // 读取最小可读字节
        int min = Math.min(available, len);
        buffer.readBytes(b, off, min);
        return min;
    }

    @Override
    public int available() throws IOException {
        return buffer.readableBytes();
    }

    @Override
    public long skip(long n) throws IOException {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }
}
