package org.maple.channel;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class ClientDemo01 {
    public static void main(String[] args) throws Exception {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));
        System.out.println("waiting...");
        TimeUnit.HOURS.sleep(1);
    }
}
