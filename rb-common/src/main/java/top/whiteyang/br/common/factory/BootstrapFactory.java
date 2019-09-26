package top.whiteyang.br.common.factory;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.BootstrapConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.List;

/**
 * Today the best performance as tomorrow newest starter! Created by IntelliJ IDEA.
 */
public interface BootstrapFactory {
    Bootstrap get(String host,int port, List<ChannelHandler> handlers);

    class DefaultBootstrapFactory implements BootstrapFactory{
        @Override public Bootstrap get(String host, int port, List<ChannelHandler> handlers) {
            EventLoopGroup loopGroup=Epoll.isAvailable()?new EpollEventLoopGroup():new NioEventLoopGroup();
            Bootstrap bootstrap=new Bootstrap();
            bootstrap.group(loopGroup);
            bootstrap.remoteAddress(host,port);
            bootstrap.channel(Epoll.isAvailable()? EpollSocketChannel.class: NioSocketChannel.class);
            if(null!=handlers&&!handlers.isEmpty()) {
                bootstrap.handler(new ChannelInitializer<Channel>() {
                    @Override protected void initChannel(Channel ch) throws Exception {
                        handlers.stream().forEach(ch.pipeline()::addLast);
                    }
                });
            }
            return bootstrap;
        }
    }
}
