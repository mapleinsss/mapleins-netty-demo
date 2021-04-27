package org.maple.ch12;

import lombok.Data;

/**
 * 数据结构
 */
@Data
public class NettyMessage {

    private Header header;
    private Object body;
}
