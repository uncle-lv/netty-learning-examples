import lombok.extern.slf4j.Slf4j;

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

@Slf4j
public class EchoServer {

    private final int PORT;

    public static void main(String[] args) {
        new EchoServer(8080).run();
    }

    public EchoServer(int PORT) {
        this.PORT = PORT;
    }

    public void run() {
        ServerSocketChannel serverSocketChannel;
        Selector selector;

        try {
            serverSocketChannel = ServerSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(PORT);
            serverSocketChannel.bind(address);
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            log.info("EchoServer has started at port: {}", PORT);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                selector.select();
            } catch (IOException e) {
                log.error("EchoServer Exception: {}", e.getMessage());
            }

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                try {
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel = server.accept();
                        log.info("Receive connection from: {}:", socketChannel);
                        socketChannel.configureBlocking(false);
                        SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                        ByteBuffer buffer = ByteBuffer.allocate(100);
                        selectionKey.attach(buffer);
                    }

                    if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        client.read(output);
                        output.flip();
                        log.info("{} --> EchoServer: {}", client.getRemoteAddress(), StandardCharsets.UTF_8.decode(output).toString());
                        key.interestOps(SelectionKey.OP_WRITE);
                    }

                    if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        output.flip();
                        client.write(output);
                        output.flip();
                        log.info("EchoServer --> {}: {}", client.getRemoteAddress(), StandardCharsets.UTF_8.decode(output).toString());
                        output.compact();
                        key.interestOps(SelectionKey.OP_READ);
                    }
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException ioe) {
                        log.error("EchoServer Exception: {}", ioe.getMessage());
                    }
                }
            }
        }
    }
}
