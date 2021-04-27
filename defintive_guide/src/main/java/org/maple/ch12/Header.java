package org.maple.ch12;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 协议头
 */
@Data
public final class Header {

    /**
     * Netty 消息的验证码，三部分组成
     *  - 0xABEF：固定值，表名该消息是 Netty 协议消息，2 个字节
     *  - 主版本号：1 ~ 255，1 个字节
     *  - 此版本好：1 ~ 255，1 个字节
     *  crcCode = 0xABEF + 主版本号 + 次版本号
     */
    private int crcCode = 0xbef0101;
    // 消息长度
    private int length;
    // 会话 ID
    private long sessionID;
    /**
     * 消息类型：
     * 0. 业务请求消息
     * 1. 业务响应消息
     * 2. 请求/响应消息
     * 3. 握手请求
     * 4. 握手应答
     * 5. 心跳请求
     * 6. 心跳答应
     */
    private byte type;
    // 消息优先级 0 ~ 255
    private byte priority;
    // 附件，扩展消息头
    private Map<String,Object> attachment = new HashMap<String,Object>();

}
