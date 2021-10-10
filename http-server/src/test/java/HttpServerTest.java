import org.junit.jupiter.api.Test;

public class HttpServerTest {

    @Test
    public void testHttpServer() throws Exception {
        new HttpServer(8080).run();
    }
}
