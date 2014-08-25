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
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.glowstone.GlowServer;
import net.glowstone.net.pipeline.GlowChannelInitializer;

import java.net.SocketAddress;

/**
 * Modified implementation of {@link com.flowpowered.networking.NetworkServer}.
 */
public class GlowNetworkServer implements ConnectionManager {
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
        return bootstrap.bind(address).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> f) throws Exception {
                if (f.isSuccess()) {
                    onBindSuccess(address);
                } else {
                    onBindFailure(address, f.cause());
                }
            }
        });
    }

    /**
     * Called when a bind is successfully made.
     * @param address The address we are now bound too.
     */
    public void onBindSuccess(SocketAddress address) {
    }

    /**
     * Called when a bind fails.
     * @param address The address we attempted to bind too.
     * @param t The cause of why the binding failed. This can be null.
     */
    public void onBindFailure(SocketAddress address, Throwable t) {
    }

    @Override
    public void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

}

