package top.whiteyang.rb.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.whiteyang.br.common.constant.Constants;
import top.whiteyang.br.common.container.BootstrapMap;
import top.whiteyang.br.common.container.ChannelMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
    private List<ChannelHandler> serverHandlers=null;

    private ServerBootstrap serverBootstrap=null;
    private Channel serverChannel=null;
    private Map<String,EventLoopGroup> loopGroupMap=new ConcurrentHashMap<>();


    private Map<String,List<ChannelHandler>> clientHandlers=new ConcurrentHashMap<>();
    private List<EventLoopGroup> clientLoopGroup=new ArrayList<>();
    private BootstrapMap bootstrapMap =new BootstrapMap();
    private ChannelMap channelMap =new ChannelMap();

    private Random random=new Random();
    private Logger logger= LoggerFactory.getLogger(ClientContext.class);
    public ClientContext(){
        init();
    }

    public synchronized void listen(int port,final List<ChannelHandler> handlers){
        if(null!=serverBootstrap){
            throw new IllegalStateException("unsupport multiple server port !");
        }
        serverBootstrap=serverBootSupplier(port);
        try {
            serverHandlers=handlers;
            if(null!=handlers&&!handlers.isEmpty()) {
                serverBootstrap.childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        handlers.forEach(ch.pipeline()::addLast);
                    }
                });
            }
            ChannelFuture channelFuture=serverBootstrap.bind().sync();
            if(channelFuture.isSuccess()){
                logger.info("bind success !");
                serverChannel=channelFuture.channel();
            }else{
                logger.error("bind fail !",channelFuture.cause());
            }
        }catch (Exception e){
            logger.error("exception occured when bind:{}",port);
            shutdown();
        }
    }
    public synchronized void connect(String host,int port,List<ChannelHandler> handlers){
        try {
            String key=host+port;
            clientHandlers.put(key,handlers);
            if(null== bootstrapMap.get(host,port)){
                Bootstrap bootstrap=bootSupplier(host,port);
                if(null!=handlers&&!handlers.isEmpty()) {
                    bootstrap.handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch){
                            handlers.forEach(ch.pipeline()::addLast);
                        }
                    });
                }
                ChannelFuture future=bootstrap.connect().sync();
                if(future.isSuccess()){
                    logger.info("connect {}:{} success !",host,port);
                    channelMap.put(host,port,future.channel());
                }else{
                    logger.error("connect {}:{} fail !",host,port,future.cause());
                }
            }
        } catch (Exception e) {
            logger.error("exception occured when connect {}:{}",host,port,e);
            shutdown();
        }
    }
    public synchronized void close(String host,int port){
        Channel channel=channelMap.get(host,port);
        if(null!=channel){
            channel.closeFuture().addListener(f->{
                if(f.isSuccess()){
                    logger.info("close channel {}:{} success !",host,port);
                }else{
                    logger.error("close channe {}:{} fail",host,port,f.cause());
                }
            });
        }
    }
    public synchronized void shutdown(){
        try {
            if(null!=serverChannel){
                serverChannel.closeFuture().addListener(f->{
                    if(f.isSuccess()){
                        logger.info("server channel close success !");
                    }else{
                        logger.error("server channel close fail ! ");
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
            logger.error("exception occured when shutdown !",e);
        }
    }
    public boolean write(String host,int port,byte[] data){
        Channel channel=channelMap.get(host,port);
        if(null!=channel){
            channel.writeAndFlush(data);
            return true;
        }else{
            throw new IllegalArgumentException(String.format("not connect to %s:%d",host,port));
        }
    }


    private ServerBootstrap serverBootSupplier(int port){
        ServerBootstrap serverBootstrap=new ServerBootstrap();
        EventLoopGroup boss=loopGroupMap.get(Constants.BOSS);
        EventLoopGroup worker=loopGroupMap.get(Constants.WORKER);
        serverBootstrap.group(boss,worker);
        serverBootstrap.channel(Epoll.isAvailable()? EpollServerSocketChannel.class: NioServerSocketChannel.class);
        serverBootstrap.localAddress(Constants.LOCAL_HOST,port);
        return serverBootstrap;
    }
    private Bootstrap bootSupplier(String host,int port){
        EventLoopGroup loopGroup;
        Bootstrap bootstrap=new Bootstrap();
        if(clientLoopGroup.size()>1){
            loopGroup=clientLoopGroup.get(random.nextInt(clientLoopGroup.size()));
        }else{
            loopGroup=clientLoopGroup.get(0);
        }
        bootstrap.group(loopGroup);
        bootstrap.remoteAddress(host,port);
        bootstrap.channel(Epoll.isAvailable()? EpollSocketChannel.class: NioSocketChannel.class);
        return bootstrap;
    }
    private void init(){
        int threadNum=Runtime.getRuntime().availableProcessors()*2;
        loopGroupMap.put(Constants.BOSS,Epoll.isAvailable()?new EpollEventLoopGroup(threadNum):new NioEventLoopGroup(threadNum));
        loopGroupMap.put(Constants.WORKER,Epoll.isAvailable()?new EpollEventLoopGroup(threadNum):new NioEventLoopGroup(threadNum));
        clientLoopGroup.add(Epoll.isAvailable()?new EpollEventLoopGroup(threadNum):new NioEventLoopGroup(threadNum));
    }

}
