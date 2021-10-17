import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Channel inChannel = ctx.channel();

        log.info("{} sends message: {}", inChannel.remoteAddress(), msg.text());
        for (Channel channel : channelGroup) {
            if (channel != inChannel) {
                channel.writeAndFlush(
                        new TextWebSocketFrame(
                                String.format("[%s] - [%s]: %s", MessageType.MESSAGE, inChannel.remoteAddress(), msg.text())
                        )
                );
            } else {
                channel.writeAndFlush(new TextWebSocketFrame(String.format("[YOU]: %s", msg.text())));
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush(
                new TextWebSocketFrame(
                        String.format("[%s]: %s has been online.", MessageType.NOTIFICATION, channel.remoteAddress())
                )
        );
        channelGroup.add(channel);
        log.info("{} has been online.", channel.remoteAddress());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush(String.format("[%s]: %s has been offline.", MessageType.NOTIFICATION, channel.remoteAddress()));
        log.info("{} has been offline.", channel.remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        log.info("{} has occurred exception.", channel.remoteAddress());
        cause.printStackTrace();
        ctx.close();
    }
}

enum MessageType {
    NOTIFICATION,
    MESSAGE
}
