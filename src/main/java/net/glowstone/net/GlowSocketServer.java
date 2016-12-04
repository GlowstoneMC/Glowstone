package net.glowstone.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.glowstone.GlowServer;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

public abstract class GlowSocketServer extends GlowNetworkServer {
    protected final EventLoopGroup bossGroup;
    protected final EventLoopGroup workerGroup;
    protected final ServerBootstrap bootstrap;

    public GlowSocketServer(GlowServer server, CountDownLatch latch) {
        super(server, latch);
        bossGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup)
                .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    public ChannelFuture bind(InetSocketAddress address) {
        return this.bootstrap.bind(address).addListener(future -> {
            if (future.isSuccess()) {
                onBindSuccess(address);
            } else {
                onBindFailure(address, future.cause());
            }
        });
    }

    public void shutdown() {
        bootstrap.childGroup().shutdownGracefully();
        bootstrap.group().shutdownGracefully();
    }
}
