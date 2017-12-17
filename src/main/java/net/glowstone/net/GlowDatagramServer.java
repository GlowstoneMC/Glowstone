package net.glowstone.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import net.glowstone.GlowServer;

public abstract class GlowDatagramServer extends GlowNetworkServer {

    protected final EventLoopGroup group;
    protected final Bootstrap bootstrap;

    public GlowDatagramServer(GlowServer server, CountDownLatch latch) {
        super(server, latch);
        boolean epoll = Epoll.isAvailable();
        group = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        bootstrap = new Bootstrap();

        bootstrap
            .group(group)
            .channel(epoll ? EpollDatagramChannel.class : NioDatagramChannel.class)
            .option(ChannelOption.SO_KEEPALIVE, true);
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
        bootstrap.config().group().shutdownGracefully();
    }
}
