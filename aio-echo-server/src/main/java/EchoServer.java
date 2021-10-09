import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class EchoServer {

    private final int PORT;

    public static void main(String[] args) {
        new EchoServer(8080).run();
    }

    public EchoServer(int PORT) {
        this.PORT = PORT;
    }

    public void run() {
        AsynchronousServerSocketChannel serverSocketChannel;

        try {
            serverSocketChannel = AsynchronousServerSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress(PORT);
            serverSocketChannel.bind(address);
            System.out.println("EchoServer has started at port: " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            Future<AsynchronousSocketChannel> future = serverSocketChannel.accept();
            AsynchronousSocketChannel socketChannel = null;

            try {
                socketChannel = future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("EchoServer Exception: " + e.getMessage());
            }
            System.out.println("Receive connection from: " + socketChannel);

            ByteBuffer buffer = ByteBuffer.allocate(100);

            try {
                while (socketChannel.read(buffer).get() != -1) {
                    buffer.flip();
                    socketChannel.write(buffer).get();

                    System.out.println("EchoServer --> " + socketChannel.getRemoteAddress() + ": " + new String(buffer.array(), "UTF-8"));

                    if (buffer.hasRemaining()) {
                        buffer.compact();
                    } else {
                        buffer.clear();
                    }
                }
                socketChannel.close();
            } catch (InterruptedException| ExecutionException | IOException e) {
                e.printStackTrace();
                System.out.println("EchoServer Exception: " + e.getMessage());
            }
        }
    }
}
