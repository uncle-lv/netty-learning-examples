import handler.RedisClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RedisClient {

    private final String HOST_NAME;
    private final int PORT;

    public static void main(String[] args) throws Exception {
        new RedisClient("127.0.0.1", 6379).connect();
    }

    public RedisClient(String HOST_NAME, int PORT) {
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
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new RedisDecoder())
                                    .addLast(new RedisBulkStringAggregator())
                                    .addLast(new RedisArrayAggregator())
                                    .addLast(new RedisEncoder())
                                    .addLast(new RedisClientHandler());
                        }
                    });

            Channel channel = bootstrap.connect().sync().channel();
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            for (;;) {
                System.out.print(">");
                final String input = in.readLine();
                final String line = input != null ? input.trim() : null;
                if (null == line || "quit".equalsIgnoreCase(line)) {
                    channel.close().sync();
                    break;
                } else if (line.isBlank()) {
                    continue;
                }

                lastWriteFuture = channel.writeAndFlush(line);
                lastWriteFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        if (!future.isSuccess()) {
                            System.err.print("write failed: ");
                            future.cause().printStackTrace(System.err);
                        }
                    }
                });
            }

            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
