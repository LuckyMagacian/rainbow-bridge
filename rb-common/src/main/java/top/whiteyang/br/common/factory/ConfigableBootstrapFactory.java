package top.whiteyang.br.common.factory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import java.util.List;
import java.util.Map;
import top.whiteyang.br.common.facade.Configable;

/**
 * Today the best performance as tomorrow newest starter! Created by IntelliJ IDEA.
 */
public interface ConfigableBootstrapFactory extends BootstrapFactory, Configable {
    default Bootstrap get(String host, int port, Map<String,Object> conf, List<ChannelHandler> handlers){
        config(conf);
        return get(host,port,handlers);
    }
}
