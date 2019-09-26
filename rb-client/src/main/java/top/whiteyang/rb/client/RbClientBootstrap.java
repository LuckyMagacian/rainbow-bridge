package top.whiteyang.rb.client;

import io.netty.channel.ChannelHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import top.whiteyang.br.common.constant.Constants;
import top.whiteyang.br.common.handler.ByteOutHandler;
import top.whiteyang.br.common.handler.InboundLogHandler;

import java.util.ArrayList;
import java.util.List;

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
 * time:2019-09-21 周六 16:55
 */
public class RbClientBootstrap {
    public static void main(String[] args) throws InterruptedException {
        ClientContext ctx=new ClientContext();
        List<ChannelHandler> list = new ArrayList<>();
        list.add(new InboundLogHandler());
        list.add(new IdleStateHandler(5,5,5));
        ctx.listen(7788, list);

        list=new ArrayList<>();
        list.add(new InboundLogHandler());
        list.add(new ByteOutHandler());
        list.add(new IdleStateHandler(5,5,5));
        ctx.connect(Constants.LOCAL_HOST,7788,list);
        ctx.write(Constants.LOCAL_HOST,7788,"hello".getBytes(CharsetUtil.UTF_8));
        Thread.sleep(3000);
        ctx.close(Constants.LOCAL_HOST,7788);
        Thread.sleep(1000);
        ctx.close();
    }

}
