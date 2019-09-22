package top.whiteyang.rb.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
 * time:2019-09-21 周六 15:22
 */
public class RbServerBootstrap {
    public static void main(String[] args){
        boolean epoll=Epoll.isAvailable();
        EventLoopGroup bossGroup= epoll?new EpollEventLoopGroup(): new NioEventLoopGroup();
        EventLoopGroup workerGroup= epoll?new EpollEventLoopGroup(): new NioEventLoopGroup();

        ServerBootstrap serverBootstrap=new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup);
        serverBootstrap.channel(epoll?EpollServerSocketChannel.class: NioServerSocketChannel.class);
        serverBootstrap.childHandler(new LogHandler());
        serverBootstrap.localAddress(9966);
        serverBootstrap.bind();
    }
}
