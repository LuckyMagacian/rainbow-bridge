package top.whiteyang.rb.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import top.whiteyang.br.common.handler.ByteOutHandler;
import top.whiteyang.br.common.handler.OutboundLogHandler;

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
        EventLoopGroup loopGroup = null;
        Bootstrap bootstrap = null;
        ChannelFuture future = null;
        try {
            boolean epoll = Epoll.isAvailable();
            loopGroup = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();

            bootstrap = new Bootstrap();
            bootstrap.group(loopGroup);
            bootstrap.channel(epoll ? EpollSocketChannel.class : NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline()
//                            .addLast(new StringEncoder())
//                            .addLast(new StringDecoder())
                            .addLast(new OutboundLogHandler())
                            .addLast(new ByteOutHandler())
                    ;
                }
            });
            bootstrap.remoteAddress("localhost", 9966);
            future = bootstrap.connect();
            ClientContext.putBootstrap("localhost",9966,bootstrap);
            future.addListener(f -> {
                if (f.isSuccess()) {
                    System.err.println("connect success !");
                    ClientContext.putChannel("localhost",9966,((ChannelFuture)f).channel());
                } else {
                    System.err.println("connect fail !");
                    f.cause().printStackTrace();
                }
            }).sync();
            waitPause();
        } catch (Exception e) {
            e.printStackTrace();
            if (null != future) {
                future.channel().closeFuture().addListener(f -> {
                    if (f.isSuccess()) {
                        System.err.println("close success !");
                    } else {
                        System.err.println("close fail !");
                    }
                });
            }
            if (null != loopGroup) {
                loopGroup.shutdownGracefully();
            }
        }
    }

    public static void waitPause() throws InterruptedException {
        while(true){
            Thread.sleep(1000);
            ClientContext.getChannel("localhost",9966).writeAndFlush("are you ok ?".getBytes(CharsetUtil.UTF_8));
        }
    }
}
