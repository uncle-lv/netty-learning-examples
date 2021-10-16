import initializer.SimpleChatClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SimpleChatClient {

    private final String HOST_NAME;
    private final int PORT;

    public static void main(String[] args) {
        try {
            new SimpleChatClient("127.0.0.1", 8081).connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SimpleChatClient(String HOST_NAME, int PORT) {
        this.HOST_NAME = HOST_NAME;
        this.PORT = PORT;
    }

    public void connect() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(HOST_NAME, PORT)
                    .handler(new SimpleChatClientInitializer());

            Channel channel = bootstrap.connect().sync().channel();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                channel.writeAndFlush(in.readLine() + "\r\n");
            }
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
