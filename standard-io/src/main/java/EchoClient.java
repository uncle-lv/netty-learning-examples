import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class EchoClient {

    private final String HOST_NAME;
    private final int PORT;

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
                System.out.println("Send EchoServer: " + in.readLine());
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
