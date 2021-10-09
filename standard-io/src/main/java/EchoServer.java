import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {

    private final int PORT;

    public static void main(String[] args) {
        new EchoServer(8080).run();
    }

    public EchoServer(int PORT) {
        this.PORT = PORT;
    }

    public void run() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("EchoServer has started at port: " + PORT);
        } catch (IOException e) {
            System.out.println("Failed to start EchoServer");
            System.out.println(e.getMessage());
        }

        try (
                Socket socket = serverSocket.accept();
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ) {
            System.out.println("Receive connection from: " + socket.getRemoteSocketAddress());

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                out.println(inputLine);
                System.out.println("EchoServer --> " + socket.getRemoteSocketAddress() + ": " + inputLine);
            }
        } catch (IOException e) {
            System.out.println("EchoServer Exception: " + e.getMessage());
        }
    }
}
