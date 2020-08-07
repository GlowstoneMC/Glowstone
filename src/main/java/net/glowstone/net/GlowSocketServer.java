package net.glowstone.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import lombok.Getter;
import net.glowstone.GlowServer;
import net.glowstone.net.protocol.ProtocolProvider;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

public abstract class GlowSocketServer extends GlowNetworkServer {

    protected final EventLoopGroup bossGroup;
    protected final EventLoopGroup workerGroup;
    protected final ServerBootstrap bootstrap;
    @Getter
    protected Channel channel;

    /**
     * Creates an instance for the specified server.
     *
     * @param server the associated GlowServer
     * @param latch The countdown latch used during server startup to wait for network server
     *         binding.
     */
    public GlowSocketServer(GlowServer server, ProtocolProvider protocolProvider,
                            CountDownLatch latch) {
        super(server, protocolProvider, latch);
        bossGroup = Networking.createBestEventLoopGroup();
        workerGroup = Networking.createBestEventLoopGroup();
        bootstrap = new ServerBootstrap();

        bootstrap
            .group(bossGroup, workerGroup)
            .channel(Networking.bestServerSocketChannel())
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public ChannelFuture bind(InetSocketAddress address) {
        ChannelFuture cfuture = this.bootstrap.bind(address).addListener(future -> {
            if (future.isSuccess()) {
                onBindSuccess(address);
            } else {
                onBindFailure(address, future.cause());
            }
        });
        channel = cfuture.channel();
        return cfuture;
    }

    @Override
    public void shutdown() {
        channel.close();
        bootstrap.config().group().shutdownGracefully();
        bootstrap.config().childGroup().shutdownGracefully();

        try {
            bootstrap.config().group().terminationFuture().sync();
            bootstrap.config().childGroup().terminationFuture().sync();
        } catch (InterruptedException e) {
            GlowServer.logger.log(Level.SEVERE, "Socket server shutdown process interrupted!",
                e);
        }
    }
}
