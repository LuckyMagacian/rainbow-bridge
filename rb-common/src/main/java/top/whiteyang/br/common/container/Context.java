package top.whiteyang.br.common.container;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import top.whiteyang.br.common.facade.Closable;

/**
 * Today the best performance as tomorrow newest starter! Created by IntelliJ IDEA.
 */
public interface Context extends Closable {
    ServerBootstrap getServerBootstrap();
    Channel getServerChannel();
    Bootstrap getBootstrap(String host,int port);
    Channel getChannel(String host,int port);
}
