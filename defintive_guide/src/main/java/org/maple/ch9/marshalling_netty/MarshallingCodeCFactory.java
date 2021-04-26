package org.maple.ch9.marshalling_netty;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

public class MarshallingCodeCFactory {

    //创建JBoss Marshalling解码器
    public static MarshallingDecoder buildMarshallingDecoder() {
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
        return new MarshallingDecoder(provider);
    }

    //创建JBoss Marshalling编码器
    public static MarshallingEncoder buildMarshallingEncoder() {
        //"serial"表示创建的是Java序列化工厂对象
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
        //MarshallingEncoder用于将实现序列化接口的POJO对象序列化为二进制字节数组
        return new MarshallingEncoder(provider);
    }
}
