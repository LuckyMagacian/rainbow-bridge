package top.whiteyang.br.common.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class HttpContentEchoHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest){
            FullHttpRequest request= (FullHttpRequest) msg;
            HttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_0,HttpResponseStatus.OK,request.content());
            ctx.writeAndFlush(response).addListener((ChannelFuture f)->{
                if(f.isDone()) {
                    f.channel().close();
                }
            });
        }
    }
}
