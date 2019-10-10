package top.whiteyang.rb.server;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import top.whiteyang.br.common.handler.inbound.EchoHandler;
import top.whiteyang.br.common.handler.inbound.HttpContentEchoHandler;
import top.whiteyang.br.common.handler.inbound.InboundLogHandler;
import top.whiteyang.br.common.handler.outbound.OutboundLogHandler;

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
 * <p>
 * Created by IntelliJ IDEA.
 *
 * @author : whiteyang
 * email: yangyuanjian@outlook.com
 * time:2019-09-21 周六 15:22
 */
public class RbServerBootstrap {
    public static void main(String[] args){
        ServerContext serverContext=new ServerContext();
        List<Supplier<ChannelHandler>> list=new ArrayList<>();
        list.add(InboundLogHandler::new);
        list.add(EchoHandler::new);
        serverContext.listen(6789,list);
    }
}
