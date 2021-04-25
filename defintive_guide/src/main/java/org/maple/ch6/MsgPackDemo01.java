package org.maple.ch6;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用 MessagePack 序列化
 * 官网：It's like JSON.but fast and small. 并且支持多语言
 */
public class MsgPackDemo01 {
    public static void main(String[] args) throws IOException {
        List<String> list = new ArrayList<>();
        list.add("大");
        list.add("big");
        MessagePack msgPack = new MessagePack();
        // 序列化
        byte[] raw = msgPack.write(list);
        // 反序列化
        System.out.println(msgPack.read(raw, Templates.tList(Templates.TString)));

    }
}
