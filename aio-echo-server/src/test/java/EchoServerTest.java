import org.junit.jupiter.api.Test;

public class EchoServerTest {

    @Test
    public void testEchoServer() {
        new EchoServer(8080).run();
    }
}
