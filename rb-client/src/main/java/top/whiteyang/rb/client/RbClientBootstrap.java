package top.whiteyang.rb.client;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.*;
import top.whiteyang.br.common.handler.inbound.HttpContentEchoHandler;
import top.whiteyang.br.common.handler.inbound.InboundLogHandler;
import top.whiteyang.br.common.handler.outbound.OutboundLogHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
 *
 * <p>
 * Created by IntelliJ IDEA.
 *
 * @author : whiteyang
 * email: yangyuanjian@outlook.com
 * time:2019-09-21 周六 16:55
 */
public class RbClientBootstrap {
    public static void main(String[] args) throws IOException {
        org.apache.log4j.PropertyConfigurator.configure("log4j.properties");
        ClientContext ctx=new ClientContext();
        List<Supplier<ChannelHandler>> list = new ArrayList<>();
        list.add(InboundLogHandler::new);
        list.add(HttpRequestDecoder::new);
        list.add(HttpResponseEncoder::new);
        list.add(OutboundLogHandler::new);

        list.add(()->new HttpObjectAggregator(65535));
        list.add(HttpContentEchoHandler::new);


        ctx.listen(7788, list);
    }

}
