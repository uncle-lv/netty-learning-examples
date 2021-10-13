import org.junit.jupiter.api.Test;

public class HeartbeatServerTest {

    @Test
    public void testHeartbeatServer() throws Exception {
        new HeartbeatServer(8081).run();
    }
}
