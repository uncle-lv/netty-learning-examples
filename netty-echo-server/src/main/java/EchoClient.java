import handler.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class EchoClient {

    private final String HOST_NAME;
    private final int PORT;

    public static void main(String[] args) {
        try {
            new EchoClient("127.0.0.1", 8080).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EchoClient(String HOST_NAME, int PORT) {
        this.HOST_NAME = HOST_NAME;
        this.PORT = PORT;
    }

    public void send() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(HOST_NAME, PORT)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new EchoClientHandler()
                            );
                        }
                    });
            ChannelFuture future = bootstrap.connect().sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
