public class Client {

    public static void main(String[] args) throws Exception {
        new SimpleChatClient("127.0.0.1", 8081).connect();
    }
}
