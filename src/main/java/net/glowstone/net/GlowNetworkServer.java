package net.glowstone.net;

import io.netty.channel.ChannelFuture;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import net.glowstone.GlowServer;

/**
 * Represents a network server.
 *
 * Modified implementation of {@link com.flowpowered.network.NetworkServer}.
 */
public abstract class GlowNetworkServer {

    private final GlowServer server;
    protected CountDownLatch latch;

    public GlowNetworkServer(GlowServer server, CountDownLatch latch) {
        this.server = server;
        this.latch = latch;
    }

    public abstract ChannelFuture bind(InetSocketAddress address);

    public GlowServer getServer() {
        return server;
    }

    public void onBindSuccess(InetSocketAddress address) {
        latch.countDown();
    }

    public abstract void onBindFailure(InetSocketAddress address, Throwable t);

    public abstract void shutdown();
}
