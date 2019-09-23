package top.whiteyang.rb.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

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
 * time:2019-09-23 周一 20:13
 */
public class ClientContext{
    private ConcurrentHashMap<String, Bootstrap> bootstraps=new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Channel> channels=new ConcurrentHashMap<>();

    private static final ClientContext ctx=new ClientContext();
    private ClientContext(){ }

    public static void putBootstrap(String remoteHost,int remotePort,Bootstrap bootstrap){
        if(null!=remoteHost) {
            ctx.bootstraps.put(remoteHost + remotePort, bootstrap);
        }else{
            throw new NullPointerException("host can't be null !");
        }
    }
    public static Bootstrap getBootstrap(String remoteHost,int remotePort){
        return ctx.bootstraps.get(remoteHost + remotePort);
    }
    public static void putChannel(String remoteHost, int remotePort, Channel channel){
        if(null!=remoteHost) {
            ctx.channels.put(remoteHost + remotePort, channel);
        }else{
            throw new NullPointerException("host can't be null !");
        }
    }
    public static Channel getChannel(String remoteHost,int remotePort){
        return ctx.channels.get(remoteHost+remotePort);
    }
}
