import com.sun.org.apache.xpath.internal.operations.String;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * Date:8/14/2021
 * Author:Zoo
 * Description:NIO实现高并发
 */
public class Step4Server {
    ServerSocketChannel ssc;

    public static void listen(int port) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(port));
        //Reactive /Reactor
        ssc.configureBlocking(false);

        Selector selector = Selector.open();

        ssc.register(selector, ssc.validOps(), null);

        ByteBuffer buffer = ByteBuffer.allocate(1024 * 16);
        //比while少一条判断指令
        for (;;) {
            int numOfKeys = selector.select();

            Set<SelectionKey> selectedKeys = selector.selectedKeys();

            Iterator it = selectedKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                if (key.isAcceptable()) {
                    //socketChannel相当于一个文件描述符
                    SocketChannel socketChannel = ssc.accept();
                    if (socketChannel == null) {
                        continue;
                    }

                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);

                } else {
                    SocketChannel SocketChannel = (SocketChannel) key.channel();
                    //把position清零
                    buffer.clear();

                    SocketChannel.read(buffer);
                    String request = new String(buffer.array());

                    //logic..
                    buffer.clear();
                    buffer.put("HTTP/1.1 200 ok\n\nHello NIO!!".getBytes());

                    buffer.flip();
                    SocketChannel.write(buffer);
                    SocketChannel.close();
                }
            }
        }
    }
}
