package org.maple.ch12.protocol;

import lombok.Data;

/**
 * 数据结构
 */
@Data
public class NettyMessage {

    private Header header;
    private Object body;
}
