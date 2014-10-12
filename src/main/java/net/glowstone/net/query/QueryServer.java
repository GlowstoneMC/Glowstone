package net.glowstone.net.query;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import net.glowstone.GlowServer;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of a server for the minecraft server query protocol.
 * @see <a href="http://wiki.vg/Query">Protocol Specifications</a>
 */
public class QueryServer {
    /**
     * The {@link EventLoopGroup} used by the query server.
     */
    private EventLoopGroup group = new NioEventLoopGroup();

    /**
     * The {@link Bootstrap} used by netty to instantiate the query server.
     */
    private Bootstrap bootstrap = new Bootstrap();

    /**
     * Instance of the GlowServer.
     */
    private GlowServer server;

    /**
     * Maps each {@link InetSocketAddress} of a client to its challenge token.
     */
    private Map<InetSocketAddress, Integer> challengeTokens = new ConcurrentHashMap<>();

    /**
     * The {@link Random} used to generate challenge tokens.
     */
    private Random random = new Random();

    /**
     * The task used to invalidate all challenge tokens every 30 seconds.
     */
    private ChallengeTokenFlushTask flushTask;

    public QueryServer(GlowServer server, boolean showPlugins) {
        this.server = server;

        bootstrap
                .group(group)
                .channel(NioDatagramChannel.class)
                .handler(new QueryHandler(this, showPlugins));
    }

    /**
     * Bind the server on the specified address.
     * @param address The address.
     * @return Netty channel future for bind operation.
     */
    public ChannelFuture bind(final SocketAddress address) {
        if (flushTask == null) {
            flushTask = new ChallengeTokenFlushTask();
            flushTask.runTaskTimerAsynchronously(null, 600, 600);
        }
        return bootstrap.bind(address);
    }

    /**
     * Shut the query server down.
     */
    public void shutdown() {
        group.shutdownGracefully();
        if (flushTask != null) {
            flushTask.cancel();
        }
    }

    /**
     * Generate a new token.
     * @param address The sender address.
     * @return The generated valid token.
     */
    public int generateChallengeToken(InetSocketAddress address) {
        int token = random.nextInt();
        challengeTokens.put(address, token);
        return token;
    }

    /**
     * Verify that the request is using the correct challenge token.
     * @param address The sender address.
     * @param token The token.
     * @return {@code true} if the token is valid.
     */
    public boolean verifyChallengeToken(InetSocketAddress address, int token) {
        return Objects.equals(challengeTokens.get(address), token);
    }

    /**
     * Invalidates all challenge tokens.
     */
    public void flushChallengeTokens() {
        challengeTokens.clear();
    }

    /**
     * Get the Server whose information are distributed by this query server.
     * @return The server instance.
     */
    public GlowServer getServer() {
        return server;
    }

    /**
     * Inner class for resetting the challenge tokens every 30 seconds.
     */
    private class ChallengeTokenFlushTask extends BukkitRunnable {
        @Override
        public void run() {
            flushChallengeTokens();
        }
    }
}
