import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class EchoClient {

    private final String HOST_NAME;
    private final int PORT;

    public EchoClient(String HOST_NAME, int PORT) {
        this.HOST_NAME = HOST_NAME;
        this.PORT = PORT;
    }

    public void send(String msg) {
        SocketChannel socketChannel = null;

        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(HOST_NAME, PORT));
        } catch (IOException e) {
            System.err.println("EchoException: " + e.getMessage());
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
                socketChannel.read(readBuffer);
                writeBuffer.clear();
                readBuffer.clear();
                System.out.println("Send message: " + message);
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
