package com.soulmate.netty.message;

import lombok.Data;
import lombok.ToString;


/**
 * @author Huaiping He
 */
@Data
@ToString
public final class NettyMessage {
    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息体
     */
    private Object body;
}
