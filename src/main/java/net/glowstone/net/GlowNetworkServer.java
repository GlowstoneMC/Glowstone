package net.glowstone.net;

import com.flowpowered.networking.ConnectionManager;
import com.flowpowered.networking.session.Session;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.glowstone.GlowServer;
import net.glowstone.net.pipeline.GlowChannelInitializer;

import java.net.SocketAddress;

/**
 * Modified implementation of {@link com.flowpowered.networking.NetworkServer}.
 */
public final class GlowNetworkServer implements ConnectionManager {
    /**
     * The {@link io.netty.bootstrap.ServerBootstrap} used to initialize Netty.
     */
    private final ServerBootstrap bootstrap = new ServerBootstrap();
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private final GlowServer server;

    public GlowNetworkServer(GlowServer server) {
        this.server = server;
        Class<? extends ServerChannel> channel;
        if (Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup();
            workerGroup = new EpollEventLoopGroup();
            channel = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            channel = NioServerSocketChannel.class;
        }
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(channel)
                .childHandler(new GlowChannelInitializer(this))
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public Session newSession(Channel c) {
        GlowSession session = new GlowSession(server, c, this);
        server.getSessionRegistry().add(session);
        return session;
    }

    @Override
    public void sessionInactivated(Session session) {
        server.getSessionRegistry().remove((GlowSession) session);
    }

    public ChannelFuture bind(final SocketAddress address) {
        return bootstrap.bind(address);
    }

    @Override
    public void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

}

