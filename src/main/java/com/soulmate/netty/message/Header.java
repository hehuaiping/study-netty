package com.soulmate.netty.message;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * @author Huaiping He
 */
@Data
@ToString
public final class Header {
    /**
     * Netty消息的校验码，它由三部分组成：
     * 1) 0xADEF: 固定值表示消息是Netty消息，2个字节
     * 2) 主版本号: 1~255 1个字节
     * 3) 0xADEF: 次版本号: 1~255 1个字节
     * crcCode= 0xADEF + 主版本号 + 次版本号
     */
    private int crcCode = 0xABEF0101;
    /**
     * 消息长度
     */
    private int length;
    /**
     * 会话ID
     */
    private long sessionID;
    /**
     * 消息类型
     */
    private byte type;
    /**
     * 消息优先级
     */
    private byte priority;
    /**
     * 可选附件
     */
    private Map<String, Object> attachment;
}
