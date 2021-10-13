package handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {

    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(
            Unpooled.copiedBuffer("Heartbeat", CharsetUtil.UTF_8)
    );

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            IdleType idleType = null;
            ByteBuf buf = null;

            if (event.state() == IdleState.READER_IDLE) {
                idleType = IdleType.READ_IDLE;
                buf = Unpooled.copiedBuffer(idleType + ": heartbeat", CharsetUtil.UTF_8);
            } else if (event.state() == IdleState.WRITER_IDLE) {
                idleType = IdleType.WRITE_IDLE;
                buf = Unpooled.copiedBuffer(idleType + ": heartbeat", CharsetUtil.UTF_8);
            } else if (event.state() == IdleState.ALL_IDLE) {
                idleType = IdleType.ALL_IDLE;
                buf = Unpooled.copiedBuffer(idleType + ": heartbeat", CharsetUtil.UTF_8);
            }

            ctx.writeAndFlush(buf).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            log.info("Timeout Type: {} --> {}", idleType, ctx.channel().remoteAddress());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf bufIn = (ByteBuf) msg;
        log.info("{} --> HeartbeatServer: {}", ctx.channel().remoteAddress(), bufIn.toString(CharsetUtil.UTF_8));
        ctx.write(bufIn);
        log.info("HeartbeatServer --> {}: {}", ctx.channel().remoteAddress(), bufIn.toString(CharsetUtil.UTF_8));
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}


enum IdleType {
    READ_IDLE, WRITE_IDLE, ALL_IDLE
}
