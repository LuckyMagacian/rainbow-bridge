package top.whiteyang.rb.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import top.whiteyang.br.common.handler.InboundLogHandler;

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
        EventLoopGroup bossGroup=null;
        EventLoopGroup workerGroup=null;
        ServerBootstrap serverBootstrap=null;
        ChannelFuture future=null;
        try {
            boolean epoll=Epoll.isAvailable();
            bossGroup= epoll?new EpollEventLoopGroup(): new NioEventLoopGroup();
            workerGroup= epoll?new EpollEventLoopGroup(): new NioEventLoopGroup();

            serverBootstrap=new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup);
            serverBootstrap.channel(epoll?EpollServerSocketChannel.class: NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline()
//                            .addLast(new StringEncoder())
//                            .addLast(new StringDecoder())
                            .addLast(new InboundLogHandler());
                }
            });
            serverBootstrap.localAddress(9966);
            future=serverBootstrap.bind().addListener(f->{
                if(f.isSuccess()){
                    System.err.println("bind success !");
                }else{
                    System.err.println("bind fail!");
                    f.cause().printStackTrace();
                }
            });
        } catch (Exception e){
            if(null!=future){
                e.printStackTrace();
                future.channel().closeFuture().addListener(f->{
                    if(f.isSuccess()){
                        System.err.println("close success !");
                    }else{
                        System.err.println("close fail !");
                        f.cause().printStackTrace();
                    }
                });
            }
            if(null!=bossGroup){
                bossGroup.shutdownGracefully();
            }
            if(null!=workerGroup){
                workerGroup.shutdownGracefully();
            }
        }
    }
}
