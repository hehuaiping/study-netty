package com.soulmate.netty.common;

/**
 * @author soulmate
 */
public final class MessageType {
    /**
     * 业务请求消息
     */
    public static final byte REQ_MSG    = 0;
    /**
     * 业务响应消息
     */
    public static final byte RESP_MSG   = 1;
    /**
     * 业务ONE WAY消息(即使请求又是响应消息)
     */
    public static final byte ONE_WAY    = 2;
    /**
     * 握手请求消息
     */
    public static final byte LOGIN_REQ  = 3;
    /**
     * 握手响应消息
     */
    public static final byte LOGIN_RESP = 4;
    /**
     * 心跳请求消息
     */
    public static final byte HEART_REQ  = 5;
    /**
     * 心跳响应消息
     */
    public static final byte HEART_RESP  = 6;
}
