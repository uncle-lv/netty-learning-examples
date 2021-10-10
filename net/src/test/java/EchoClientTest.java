import org.junit.jupiter.api.Test;

public class EchoClientTest {

    @Test
    public void testEchoClient() {
        new EchoClient("127.0.0.1", 8080).send("Hello");
    }
}
