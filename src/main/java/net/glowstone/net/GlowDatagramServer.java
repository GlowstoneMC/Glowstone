package net.glowstone.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import net.glowstone.GlowServer;

public abstract class GlowDatagramServer extends GlowNetworkServer {

    protected final EventLoopGroup group;
    protected final Bootstrap bootstrap;

    /**
     * Creates an instance for the specified server.
     *
     * @param server the associated GlowServer
     * @param latch The countdown latch used during server startup to wait for network server
     *         binding.
     */
    public GlowDatagramServer(GlowServer server, CountDownLatch latch) {
        super(server, latch);
        group = Networking.createBestEventLoopGroup();
        bootstrap = new Bootstrap();

        bootstrap
            .group(group)
            .channel(Networking.bestDatagramChannel())
            .option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public ChannelFuture bind(InetSocketAddress address) {
        return this.bootstrap.bind(address).addListener(future -> {
            if (future.isSuccess()) {
                onBindSuccess(address);
            } else {
                onBindFailure(address, future.cause());
            }
        });
    }

    @Override
    public void shutdown() {
        bootstrap.config().group().shutdownGracefully();

        try {
            bootstrap.config().group().terminationFuture().sync();
        } catch (InterruptedException e) {
            GlowServer.logger.log(Level.SEVERE,
                "Datagram server shutdown process interrupted!",
                e);
        }
    }
}
