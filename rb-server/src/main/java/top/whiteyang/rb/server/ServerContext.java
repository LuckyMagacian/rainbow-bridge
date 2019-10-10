package top.whiteyang.rb.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.whiteyang.br.common.facade.Closeable;
import top.whiteyang.br.common.facade.Initializable;
import top.whiteyang.br.common.factory.BootstrapFactory;
import top.whiteyang.br.common.factory.ServerBootstrapFactory;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
public class ServerContext  implements Initializable, Closeable {
    /** serverbootstrap */
    private ServerBootstrap serverBootstrap;
    private ServerChannel serverChannel;
    private List<Supplier<ChannelHandler>> serverHandlerSuppiler;

    private EventLoopGroup serverBossGroup;
    private EventLoopGroup serverWorkerGroup;

    private EventLoopGroup clientGroup;

    private ServerBootstrapFactory serverBootstrapFactory;
    private BootstrapFactory bootstrapFactory;
    private Logger log= LoggerFactory.getLogger(ServerContext.class);
    public ServerContext() {
        init();
    }

    public synchronized void listen(final int port,final List<Supplier<ChannelHandler>> serverHandlerSuppiler){
        if(null!=serverBootstrap){
            throw new IllegalStateException("unsupport multiple server port !");
        }
        this.serverHandlerSuppiler=serverHandlerSuppiler;
        serverBootstrap=serverBootstrapFactory.get(port,serverHandlerSuppiler);
        try {
            ChannelFuture bindFuture = serverBootstrap.bind().sync();
            if(bindFuture.isSuccess()){
                log.info("bind success !");
                serverChannel= (ServerChannel) bindFuture.channel();
            }else{
                log.error("bind fail !",bindFuture.cause());
            }
        }catch (Exception e){
            log.error("exception occured when bind:{}",port);
            close();
        }
    }

    @Override
    public void init() {
        int threadNum=Runtime.getRuntime().availableProcessors()*2;
        serverBossGroup= Epoll.isAvailable()?new EpollEventLoopGroup(threadNum):new NioEventLoopGroup(threadNum);
        serverWorkerGroup=Epoll.isAvailable()?new EpollEventLoopGroup(threadNum):new NioEventLoopGroup(threadNum);
        clientGroup=Epoll.isAvailable()?new EpollEventLoopGroup(threadNum):new NioEventLoopGroup(threadNum*2);
        bootstrapFactory=new BootstrapFactory.DefaultBootstrapFactory();
        serverBootstrapFactory=new ServerBootstrapFactory.DefaultServerBootstrapFactory();
    }

    @Override
    public void close() {
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
            if(null!=serverBossGroup&&!serverBossGroup.isShutdown()){
                serverBossGroup.shutdownGracefully();
            }
            if(null!=serverWorkerGroup&&!serverWorkerGroup.isShutdown()){
                serverBossGroup.shutdownGracefully();
            }
            if(null!=clientGroup&&!clientGroup.isShutdown()){
                clientGroup.shutdownGracefully();
            }
        } catch (Exception e) {
            log.error("exception occured when shutdown !",e);
        }
    }
}
