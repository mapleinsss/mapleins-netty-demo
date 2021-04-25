package org.maple.buffer;


import lombok.extern.log4j.Log4j2;

import java.nio.ByteBuffer;

/**
 * allocate()
 */
@Log4j2
public class ByteBufferDemo04 {
    public static void main(String[] args) {
        // HeapByteBuffer 堆内存，读写效率低，受到 GC 的影响
        log.debug(ByteBuffer.allocate(16).getClass());
        // DirectByteBuffer 直接内存，读写效率高（少一次拷贝），不会受到 GC 影响
        // 需要调用操作系统函数分配，所以分配效率低，释放不合理容易内存泄露
        log.debug(ByteBuffer.allocateDirect(16).getClass());
    }
}
