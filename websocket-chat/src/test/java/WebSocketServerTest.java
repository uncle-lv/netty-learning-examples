import org.junit.jupiter.api.Test;

public class WebSocketServerTest {

    @Test
    public void testWebSocketServer() throws Exception {
        new WebSocketChatServer(8081).run();
    }
}
