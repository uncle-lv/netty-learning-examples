import org.junit.jupiter.api.Test;

public class EchoClientTest {

    @Test
    public void testEchoClient() throws Exception {
        new EchoClient("127.0.0.1", 8080).send();
    }
}
