package org.maple.ch8.protobuf_test;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.log4j.Log4j2;

/**
 * 使用 protobuf 序列化和反序列化
 */
@Log4j2
public class ProtoBufTest {
    public static void main(String[] args) throws InvalidProtocolBufferException {

        Person.person person = Person.person.newBuilder()
                .setId(1L)
                .setName("Allen")
                .setAge(18)
                .build();
        log.debug("使用 ProtoBuf 生成的 Java 对象构建 Person 为 \r\n {} ", person);

        byte[] bytes = person.toByteArray();
        log.debug("序列化后的字节数组为 {}， 长度为 {}", bytes, bytes.length);

        Person.person p = Person.person.parseFrom(bytes);
        log.debug("反列化后的 Person 为 \r\n {}", p);
    }
}
