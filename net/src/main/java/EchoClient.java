import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

@Slf4j
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
        try (
                Socket socket = new Socket(HOST_NAME, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader stdin = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(msg.getBytes(StandardCharsets.UTF_8))));
                ) {
            String message;
            while ((message = stdin.readLine()) != null) {
                out.println(message);
                log.info("EchoClient --> {}: {}", socket.getRemoteSocketAddress(), message);
                log.info("{} --> EchoClient: {}",socket.getRemoteSocketAddress(), in.readLine());
            }
        } catch (UnknownHostException e) {
            log.error("Unknown Host, host name is: {}", HOST_NAME);
            System.exit(1);
        } catch (IOException e) {
            log.error("Failed to get I/O from host, host name is: {}", HOST_NAME);
            System.exit(1);
        }
    }
}
