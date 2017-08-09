package net.glowstone.net.query;

import io.netty.channel.ChannelFuture;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowDatagramServer;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of a server for the minecraft server query protocol.
 *
 * @see <a href="http://wiki.vg/Query">Protocol Specifications</a>
 */
public class QueryServer extends GlowDatagramServer {
    /**
     * Maps each {@link InetSocketAddress} of a client to its challenge token.
     */
    private Map<InetSocketAddress, Integer> challengeTokens = new ConcurrentHashMap<>();

    /**
     * The task used to invalidate all challenge tokens every 30 seconds.
     */
    private ChallengeTokenFlushTask flushTask;

    public QueryServer(GlowServer server, CountDownLatch latch, boolean showPlugins) {
        super(server, latch);
        bootstrap.handler(new QueryHandler(this, showPlugins));
    }

    /**
     * Bind the server on the specified address.
     *
     * @param address The address.
     * @return Netty channel future for bind operation.
     */
    public ChannelFuture bind(InetSocketAddress address) {
        GlowServer.logger.info(GlowServer.lang.getString("query.bind.address", address));
        if (flushTask == null) {
            flushTask = new ChallengeTokenFlushTask();
            flushTask.runTaskTimerAsynchronously(null, 600, 600);
        }
        return super.bind(address);
    }

    /**
     * Shut the query server down.
     */
    public void shutdown() {
        super.shutdown();
        if (flushTask != null) {
            flushTask.cancel();
        }
    }

    /**
     * Generate a new token.
     *
     * @param address The sender address.
     * @return The generated valid token.
     */
    public int generateChallengeToken(InetSocketAddress address) {
        int token = ThreadLocalRandom.current().nextInt();
        challengeTokens.put(address, token);
        return token;
    }

    /**
     * Verify that the request is using the correct challenge token.
     *
     * @param address The sender address.
     * @param token   The token.
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

    @Override
    public void onBindSuccess(InetSocketAddress address) {
        GlowServer.logger.info(GlowServer.lang.getString("query.bind.success", address));
        super.onBindSuccess(address);
    }

    @Override
    public void onBindFailure(InetSocketAddress address, Throwable t) {
        GlowServer.logger.warning(GlowServer.lang.getString("query.bind.failed", address));
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
