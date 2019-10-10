package top.whiteyang.rb.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.whiteyang.br.common.constant.Constants;
import top.whiteyang.br.common.container.BootstrapMap;
import top.whiteyang.br.common.container.ChannelMap;
import top.whiteyang.br.common.container.Context;
import top.whiteyang.br.common.facade.Closeable;
import top.whiteyang.br.common.facade.Initializable;
import top.whiteyang.br.common.factory.BootstrapFactory;
import top.whiteyang.br.common.factory.ServerBootstrapFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

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
public class ClientContext implements Context, Closeable, Initializable {
    /**serverbootstrap's childhandler*/
    private List<Supplier<ChannelHandler>> serverHandlerSupplier=null;
    /**serverbootstrap*/
    private ServerBootstrap serverBootstrap=null;
    /**server channel*/
    private ServerChannel serverChannel=null;
    /**
     * Map{
     *     "boss":bossLoopGroup,
     *     "worker":workerLoopGroup
     * }
     */
    private Map<String,EventLoopGroup> loopGroupMap=new ConcurrentHashMap<>();

    /**
     * Map{
     *     key = host:port
     *     value = bootstrap's handler
     * }
     */
    private Map<String,List<Supplier<ChannelHandler>>> handlerSuppliers =new ConcurrentHashMap<>();
    /**
     * bootstrap's loopgroup list
     */
    private List<EventLoopGroup> clientLoopGroup=new ArrayList<>();
    /**
     * bootstrapmap
     * host:port -> boostrap
     * boostrap -> host:port
     */
    private BootstrapMap bootstrapMap =new BootstrapMap();
    /**
     * channel map
     * host:port -> channel
     * channel -> host:port
     */
    private ChannelMap channelMap =new ChannelMap();
    private Logger log= LoggerFactory.getLogger(ClientContext.class);
    private BootstrapFactory bootstrapFactory=null;
    private ServerBootstrapFactory serverBootstrapFactory=null;
    public ClientContext(){
        init();
    }
    /**
     * client listen local port
     * @param port listen port
     * @param suppliers serverboostrap's handlers
     */
    public synchronized void listen(int port,final List<Supplier<ChannelHandler>> suppliers){
        if(null!=serverBootstrap){
            throw new IllegalStateException("unsupport multiple server port !");
        }
        serverBootstrap=serverBootstrapFactory.get(port,suppliers);
        try {
            serverHandlerSupplier=suppliers;
            ChannelFuture channelFuture=serverBootstrap.bind().sync();
            if(channelFuture.isSuccess()){
                log.info("bind success !");
                serverChannel= (ServerChannel) channelFuture.channel();
            }else{
                log.error("bind fail !",channelFuture.cause());
            }
        }catch (Exception e){
            log.error("exception occured when bind:{}",port);
            close();
        }
    }

    /**
     * create boostrap & channel to host:port
     * @param host remote host
     * @param port remote port
     * @param suppliers handler'supp;ier
     */
    public synchronized void connect(String host,int port,List<Supplier<ChannelHandler>> suppliers){
        try {
            String key=host+port;
            handlerSuppliers.put(key,suppliers);
            if(null== bootstrapMap.get(host,port)){
                Bootstrap bootstrap=bootstrapFactory.get(host,port,suppliers);
                ChannelFuture future=bootstrap.connect().sync();
                if(future.isSuccess()){
                    log.info("connect {}:{} success !",host,port);
                    channelMap.put(host,port,future.channel());
                }else{
                    log.error("connect {}:{} fail !",host,port,future.cause());
                }
            }
        } catch (Exception e) {
            log.error("exception occured when connect {}:{}",host,port,e);
            close();
        }
    }

    /**
     * close remote channel (host:port)
     * @param host
     * @param port
     */
    public synchronized void close(String host,int port){
        Channel channel=channelMap.get(host,port);
        if(null!=channel){
            channel.closeFuture().addListener(f->{
                if(f.isSuccess()){
                    log.info("close channel {}:{} success !",host,port);
                }else{
                    log.error("close channe {}:{} fail",host,port,f.cause());
                }
            });
        }
    }
    /**
     * write data to remote host:port
     * @param host remote host
     * @param port remote port
     * @param data data
     * @return return true on success else return false
     */
    public boolean write(String host,int port,byte[] data){
        Channel channel=channelMap.get(host,port);
        if(null!=channel){
            channel.writeAndFlush(data);
            return true;
        }else{
            throw new IllegalArgumentException(String.format("not connect to %s:%d",host,port));
        }
    }


    /**
     * init loopgroup
     */
    @Override
    public void init(){
        int threadNum=Runtime.getRuntime().availableProcessors()*2;
        loopGroupMap.put(Constants.BOSS,Epoll.isAvailable()?new EpollEventLoopGroup(threadNum):new NioEventLoopGroup(threadNum));
        loopGroupMap.put(Constants.WORKER,Epoll.isAvailable()?new EpollEventLoopGroup(threadNum):new NioEventLoopGroup(threadNum));
        clientLoopGroup.add(Epoll.isAvailable()?new EpollEventLoopGroup(threadNum):new NioEventLoopGroup(threadNum));
        bootstrapFactory=new BootstrapFactory.DefaultBootstrapFactory();
        serverBootstrapFactory=new ServerBootstrapFactory.DefaultServerBootstrapFactory();
    }
    /**
     * shutdown client
     * close loopgroup & channel
     */
    @Override
    public synchronized void close(){
        try {
            if(null!=serverChannel){
                serverChannel.closeFuture().addListener(f->{
                    if(f.isSuccess()){
                        log.info("server channel close success !");
                    }else{
                        log.error("server channel close fail ! ");
                        f.cause().printStackTrace();
                    }
                });
            }
            if(!channelMap.isEmpty()){
                channelMap.channels().forEach(Channel::closeFuture);
            }
            if(!loopGroupMap.isEmpty()){
                loopGroupMap.values().forEach(EventLoopGroup::shutdownGracefully);
            }
            if(!clientLoopGroup.isEmpty()){
                clientLoopGroup.forEach(EventLoopGroup::shutdownGracefully);
            }
        } catch (Exception e) {
            log.error("exception occured when shutdown !",e);
        }
    }
    @Override public ServerBootstrap getServerBootstrap() {
        return serverBootstrap;
    }

    @Override public Channel getServerChannel() {
        return serverChannel;
    }

    @Override public Bootstrap getBootstrap(String host, int port) {
        return bootstrapMap.get(host,port);
    }

    @Override public Channel getChannel(String host, int port) {
        return channelMap.get(host,port);
    }
}
