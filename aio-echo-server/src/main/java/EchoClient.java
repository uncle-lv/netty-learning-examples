import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

public class EchoClient {

    private final String HOST_NAME;
    private final int PORT;

    public static void main(String[] args) {
        new EchoClient("127.0.0.1", 8080).send("Hello");
    }

    public EchoClient(String HOST_NAME, int PORT) {
        this.HOST_NAME = HOST_NAME;
        this.PORT = PORT;
    }

    public void send(String msg) {
        AsynchronousSocketChannel socketChannel = null;

        try {
            socketChannel = AsynchronousSocketChannel.open();
            socketChannel.connect(new InetSocketAddress(HOST_NAME, PORT));
        } catch (IOException e) {
            System.out.println("EchoServer Exception: " + e.getMessage());
            System.exit(1);
        }

        ByteBuffer writeBuffer = ByteBuffer.allocate(32);
        ByteBuffer readBuffer = ByteBuffer.allocate(32);

        try (BufferedReader stdin = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(msg.getBytes(StandardCharsets.UTF_8))))) {
            String message;
            while ((message = stdin.readLine()) != null) {
                writeBuffer.put(message.getBytes());
                writeBuffer.flip();
                writeBuffer.rewind();
                socketChannel.write(writeBuffer);
                writeBuffer.flip();
                System.out.println("EchoClient --> " + socketChannel.getRemoteAddress() + ": " + StandardCharsets.UTF_8.decode(writeBuffer).toString());
                socketChannel.read(readBuffer);
                readBuffer.flip();
                System.out.println(socketChannel.getRemoteAddress() + " --> EchoClient: " + StandardCharsets.UTF_8.decode(writeBuffer).toString());
                writeBuffer.clear();
                readBuffer.clear();
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown Host, host name is: " + HOST_NAME);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Failed to get I/O from host, host name is: " + HOST_NAME);
            System.exit(1);
        }
    }
}
