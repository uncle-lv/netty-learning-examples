package handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleChatServerHandler extends SimpleChannelInboundHandler<String> {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("{} has been online.", channel.remoteAddress());
        channelGroup.writeAndFlush(String.format("[%s]: %s has been online.", MessageType.NOTIFICATION, channel.remoteAddress()));
        channelGroup.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("{} has been offline.", channel.remoteAddress());
        channelGroup.writeAndFlush(String.format("[%s]: %s has been offline.", MessageType.NOTIFICATION, channel.remoteAddress()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel inChannel = ctx.channel();
        for (Channel channel: channelGroup) {
            if (channel != inChannel) {
                log.info("SimpleChatServer --> {}: {}", channel.remoteAddress(), msg);
                channelGroup.writeAndFlush(String.format("[%s] - [%s]: %s", MessageType.MESSAGE, inChannel.remoteAddress(), msg));
            } else {
                log.info("SimpleChatServer --> {}: {}", channel.remoteAddress(), msg);
                channel.writeAndFlush(String.format("[%s] - [You]: %s\n", MessageType.MESSAGE, msg));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        log.info("{} has occur an exception.", channel.remoteAddress());
        cause.printStackTrace();
        ctx.close();
    }
}

enum MessageType {
    NOTIFICATION,
    MESSAGE
}
