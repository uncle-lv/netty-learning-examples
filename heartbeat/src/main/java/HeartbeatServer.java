import initializer.HeartbeatHandlerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HeartbeatServer {

    private final int PORT;

    public static void main(String[] args) {
        try {
            new HeartbeatServer(8081).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HeartbeatServer(int PORT) {
        this.PORT = PORT;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 50)
                    .childHandler(new HeartbeatHandlerInitializer());

            ChannelFuture future = bootstrap.bind(PORT).sync();
            log.info("HeartbeatServer has started at {}", PORT);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }

}
