package top.whiteyang.br.common.factory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.List;
import java.util.function.Supplier;

import top.whiteyang.br.common.constant.Constants;

/**
 * Today the best performance as tomorrow newest starter! Created by IntelliJ IDEA.
 */
public interface ServerBootstrapFactory {
    ServerBootstrap get(int port, List<Supplier<ChannelHandler>> suppliers);
    class DefaultServerBootstrapFactory implements ServerBootstrapFactory{

        @Override public ServerBootstrap get(int port, List<Supplier<ChannelHandler>> suppliers) {
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            EventLoopGroup boss=Epoll.isAvailable()?new EpollEventLoopGroup():new NioEventLoopGroup();
            EventLoopGroup worker=boss;
            serverBootstrap.group(boss,worker);
            serverBootstrap.channel(Epoll.isAvailable()? EpollServerSocketChannel.class: NioServerSocketChannel.class);
            serverBootstrap.localAddress(Constants.LOCAL_HOST,port);
            if(null!=suppliers&&!suppliers.isEmpty()){
                serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
                    @Override protected void initChannel(Channel ch) throws Exception {
                        suppliers.stream().map(Supplier::get).forEach(ch.pipeline()::addLast);
                    }
                });
            }
            return serverBootstrap;
        }
    }
}
