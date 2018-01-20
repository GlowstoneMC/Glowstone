package net.glowstone.net;

import io.netty.channel.ChannelFuture;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import lombok.Getter;
import net.glowstone.GlowServer;

/**
 * Represents a network server.
 *
 * <p>Modified implementation of {@link com.flowpowered.network.NetworkServer}.
 */
public abstract class GlowNetworkServer {

    @Getter
    private final GlowServer server;
    protected CountDownLatch latch;

    /**
     * Creates an instance for the specified server.
     *
     * @param server the associated GlowServer
     * @param latch The countdown latch used during server startup to wait for network server
     *         binding.
     */
    public GlowNetworkServer(GlowServer server, CountDownLatch latch) {
        this.server = server;
        this.latch = latch;
    }

    public abstract ChannelFuture bind(InetSocketAddress address);

    public void onBindSuccess(InetSocketAddress address) {
        latch.countDown();
    }

    public abstract void onBindFailure(InetSocketAddress address, Throwable t);

    public abstract void shutdown();
}
