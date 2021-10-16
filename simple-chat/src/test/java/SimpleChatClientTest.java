import org.junit.jupiter.api.Test;

public class SimpleChatClientTest {

    @Test
    public void testSimpleChatClient() throws Exception {
        new SimpleChatClient("127.0.0.1", 8081).connect();
    }
}
