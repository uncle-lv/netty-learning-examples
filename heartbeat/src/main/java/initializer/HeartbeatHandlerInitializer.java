package initializer;

import handler.HeartbeatServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class HeartbeatHandlerInitializer extends ChannelInitializer<Channel> {

    private static final int READ_IDLE_TIME_OUT = 5;
    private static final int WRITE_IDLE_TIME_OUT = 6;
    private static final int ALL_IDLE_TIME_OUT = 7;

    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new IdleStateHandler(
                READ_IDLE_TIME_OUT,
                WRITE_IDLE_TIME_OUT,
                ALL_IDLE_TIME_OUT,
                TimeUnit.SECONDS
                ));
        pipeline.addLast(new HeartbeatServerHandler());
    }
}
