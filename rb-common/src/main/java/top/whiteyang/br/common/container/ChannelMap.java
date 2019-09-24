package top.whiteyang.br.common.container;

import io.netty.channel.Channel;
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
 * time:2019-09-24 周二 23:22
 */
public class ChannelMap{
    private Map<String,Channel> hostMapChannel=new ConcurrentHashMap<>();
    private Map<Channel,String> channelMapHost=new ConcurrentHashMap<>();

    public void put(String host,int port,Channel channel){
        String key=host+Constants.HOST_PORT_SEPARATOR+port;
        hostMapChannel.put(key,channel);
        channelMapHost.put(channel,key);
    }
    public Channel get(String host,int port){
        String key=host+Constants.HOST_PORT_SEPARATOR+port;
        return hostMapChannel.get(key);
    }
    public String get(Channel channel){
        return channelMapHost.get(channel);
    }
    public String getHost(Channel channel){
        String host=get(channel);
        return host.substring(0,host.indexOf(Constants.HOST_PORT_SEPARATOR));
    }
    public int getPort(Channel channel){
        String host=get(channel);
        String port=host.substring(host.indexOf(Constants.HOST_PORT_SEPARATOR)+1);
        return Integer.parseInt(port);
    }
    public boolean isEmpty(){
        return hostMapChannel.isEmpty()&&channelMapHost.isEmpty();
    }
    public Collection<Channel> channels(){
        return hostMapChannel.values();
    }
}
