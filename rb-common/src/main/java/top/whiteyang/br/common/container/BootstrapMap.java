package top.whiteyang.br.common.container;

import io.netty.bootstrap.Bootstrap;
import top.whiteyang.br.common.constant.Constants;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
 * time:2019-09-24 周二 23:31
 */
public class BootstrapMap {
    private Map<String, Bootstrap> hostMapBootstrap=new ConcurrentHashMap<>();
    private Map<Bootstrap,String> bootstrapMapHost=new ConcurrentHashMap<>();

    public void put(String host, int port, Bootstrap bootstrap){
        String key=host+ Constants.HOST_PORT_SEPARATOR+port;
        hostMapBootstrap.put(key,bootstrap);
        bootstrapMapHost.put(bootstrap,key);
    }
    public Bootstrap get(String host,int port){
        String key=host+Constants.HOST_PORT_SEPARATOR+port;
        return hostMapBootstrap.get(key);
    }
    public String get(Bootstrap bootstrap){
        return bootstrapMapHost.get(bootstrap);
    }
    public String getHost(Bootstrap bootstrap){
        String host=get(bootstrap);
        return host.substring(0,host.indexOf(Constants.HOST_PORT_SEPARATOR));
    }
    public int getPort(Bootstrap bootstrap){
        String host=get(bootstrap);
        String port=host.substring(host.indexOf(Constants.HOST_PORT_SEPARATOR)+1);
        return Integer.parseInt(port);
    }
    public boolean isEmpty(){
        return hostMapBootstrap.isEmpty()&&bootstrapMapHost.isEmpty();
    }
    public Collection<Bootstrap> channels(){
        return hostMapBootstrap.values();
    }
}
