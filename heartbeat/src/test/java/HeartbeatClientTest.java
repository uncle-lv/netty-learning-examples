import org.junit.jupiter.api.Test;

public class HeartbeatClientTest {

    @Test
    public void testHeartbeatClient() throws Exception {
        new HeartbeatClient("127.0.0.1", 8081).send();
    }
}
