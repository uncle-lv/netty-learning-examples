import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class WebSocketChatServer {

    private final int PORT;

    public static void main(String[] args) {
        try {
            new WebSocketChatServer(8081).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WebSocketChatServer(int PORT) {
        this.PORT = PORT;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(PORT))
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new WebSocketChatServerInitializer());

            ChannelFuture future = bootstrap.bind().sync();
            log.info("WebSocketChatServer has started at {}", PORT);
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully().sync();
            bossGroup.shutdownGracefully().sync();
            log.info("WebSocketChatServer has been closed.");
        }
    }
}
