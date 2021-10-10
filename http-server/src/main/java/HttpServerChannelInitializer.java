import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;

public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    public HttpServerChannelInitializer() {

    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast("codec", new HttpServerCodec());
        socketChannel.pipeline().addLast("aggregator", new HttpObjectAggregator(1048576));
        socketChannel.pipeline().addLast("serverHandler", new HttpServerHandler());
    }
}
