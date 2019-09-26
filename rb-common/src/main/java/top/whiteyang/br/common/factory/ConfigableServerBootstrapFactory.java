package top.whiteyang.br.common.factory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import java.util.List;
import java.util.Map;
import top.whiteyang.br.common.facade.Configable;

/**
 * Today the best performance as tomorrow newest starter! Created by IntelliJ IDEA.
 */
public interface ConfigableServerBootstrapFactory extends ServerBootstrapFactory, Configable {
    default ServerBootstrap get(int port , Map<String,Object> conf, List<ChannelHandler> childHandlers){
        config(conf);
        return get(port,childHandlers);
    }
}
