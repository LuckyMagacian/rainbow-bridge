package top.whiteyang.br.common.handler.outbound;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * <p>
 * Today the best performance as tomorrow newest starter!
 * <p>
 * Created by IntelliJ IDEA.
 *
 * @author : whiteyang
 * email: yangyuanjian@outlook.com
 * time:2019-09-23 周一 23:54
 */
@ChannelHandler.Sharable
public class ByteOutHandler extends ChannelOutboundHandlerAdapter {
    private static final Logger LOGGER= LoggerFactory.getLogger(ByteOutHandler.class);
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof byte[]){
            LOGGER.info("convert byte[] to ByteBuf !");
            ctx.write(Unpooled.wrappedBuffer((byte[]) msg),promise);
        }else {
            super.write(ctx, msg, promise);
        }
    }
}
