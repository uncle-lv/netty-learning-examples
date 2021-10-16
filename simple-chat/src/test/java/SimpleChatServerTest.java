import org.junit.jupiter.api.Test;

public class SimpleChatServerTest {

    @Test
    public void testSimpleChatServer() throws Exception {
        new SimpleChatServer(8081).run();
    }
}
