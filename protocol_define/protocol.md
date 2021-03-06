# Netty 传输协议定义

## Netty 消息头定义
| 名称    | 类型     |  长度   | 描述 |
| ----   | ----    |  ----  | ---- |
| header | Header  | 变长   | 消息头定义 |
| body   | Object  | 变长   | 对于请求消息，它是方法的参数<br/>对于响应消息，它是返回值 |

## Netty协议消息头定义 (Header)
| 名称       | 类型     |  长度   | 描述 |
| ----      | ----     |  ----   | ---- |
| crcCode   | 整形int  | 32      | Netty消息的校验码，它由三部分组成：<br/>1) 0xADEF: 固定值表示消息是Netty消息，2个字节<br/>2) 主版本号: 1~255 1个字节<br/>3) 0xADEF: 次版本号: 1~255 1个字节<br/> crcCode= 0xADEF + 主版本号 + 次版本号|
| length    | 整形int  | 32      | 消息长度，整个消息，包括消息头和消息体 |
| sessionID | 长整形long  | 64   | 集群节点内部全局唯一，由会话ID生成器生成 |
| typo      | Byte  | 8      | 0：业务请求消息<br/>1：业务响应消息<br/>2：业务ONE WAY消息(即使请求又是响应消息)<br/>3：握手请求消息<br/>4：握手应答消息<br/>5：心跳请求消息<br/>6：心跳应答消息<br/> |
| priority  | Byte  | 8   | 消息优先级：0~255 |
| attachment | Map<String, Object> | 变长 | 可选字段，用于扩展消息头 |


