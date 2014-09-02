package net.glowstone.net;

import com.flowpowered.networking.ConnectionManager;
import com.flowpowered.networking.session.Session;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
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
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private final GlowServer server;

    public GlowNetworkServer(GlowServer server) {
        this.server = server;
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new GlowChannelInitializer(this))
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public Session newSession(Channel c) {
        GlowSession session = new GlowSession(server, c);
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

