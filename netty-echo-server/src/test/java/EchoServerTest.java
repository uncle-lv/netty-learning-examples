import org.junit.jupiter.api.Test;

public class EchoServerTest {

    @Test
    public void testEchoServer() throws Exception {
        new EchoServer(8080).run();
    }
}
