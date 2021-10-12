import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;

@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String CONTENT_TYPE = "text/html";

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) throws Exception {
        this.readRequest(request);
        log.info("{} {} {}", request.method(), request.uri(), request.protocolVersion());

        String path;
        String uri = request.uri();

        switch (uri) {
            case "/":
                path = "/www/index.html";
                break;
            case "/login":
                path = "/www/login.html";
                break;
            default:
                path = "/www/404.html";
        }

        this.writeResponse(channelHandlerContext, path);
    }

    private void readRequest(FullHttpRequest request) {
        log.info("====== request line ======");
        log.info("{} {} {}", request.method(), request.uri(), request.protocolVersion());

        log.info("====== request headers ======");
        for (String name: request.headers().names()) {
            log.info("{}: {}", name, request.headers().get(name));
        }

        log.info("====== request body ======");
        log.info(request.content().toString(CharsetUtil.UTF_8));
    }

    private void writeResponse(ChannelHandlerContext ctx, String path) throws Exception {
        File file = new File(this.getClass().getResource(path).toURI());
        ByteBuf buf = Unpooled.copiedBuffer(FileUtils.readFileToString(file, CharsetUtil.UTF_8), CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, CONTENT_TYPE);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }
}
