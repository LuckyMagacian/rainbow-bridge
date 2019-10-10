package top.whiteyang.br.common.handler.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

/**
 * │＼＿＿╭╭╭╭╭＿＿／│
 * │　　　　　　　　　│
 * │　　　　　　　　　│
 * │　－　　　　　　－│
 * │≡　　　　ｏ　≡   │
 * │　　　　　　　　　│
 * ╰——┬Ｏ◤▽◥Ｏ┬———╯
 * ｜　　ｏ　　｜
 * ｜╭－－－－╮｜
 * Created by IntelliJ IDEA.
 *
 * @author : whiteyang
 * @email: yangyuanjian@souche.com
 * @time: 2019/10/10 5:46 下午
 */
public class EchoHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof ByteBuf) {
            ctx.writeAndFlush(Unpooled.wrappedBuffer("HTTP/1.0 200 OK\r\n { }".getBytes(CharsetUtil.UTF_8))).addListener((ChannelFuture f) -> {
                System.err.println(f.cause());
                f.channel().close();
            });
        }else{
            super.channelRead(ctx,msg);
        }
    }
}
