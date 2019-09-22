package top.whiteyang.rb.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import top.whiteyang.br.common.handler.LogHandler;

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

        boolean epoll= Epoll.isAvailable();
        EventLoopGroup loopGroup=epoll?new EpollEventLoopGroup():new NioEventLoopGroup();

        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(loopGroup);
        bootstrap.channel(epoll?EpollSocketChannel.class: NioSocketChannel.class);
        bootstrap.handler(new LogHandler());
        bootstrap.connect("localhost",9966);
        bootstrap.register().sync();
    }
}
