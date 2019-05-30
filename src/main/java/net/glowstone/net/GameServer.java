package net.glowstone.net;

import com.flowpowered.network.ConnectionManager;
import com.flowpowered.network.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import net.glowstone.GlowServer;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.net.pipeline.GlowChannelInitializer;


public final class GameServer extends GlowSocketServer implements ConnectionManager {

    private static String formatAddress(InetSocketAddress socketAddress) {
        StringBuilder out = new StringBuilder();
        InetAddress ipAddress = socketAddress.getAddress();
        if (ipAddress instanceof Inet6Address) {
            // e.g. [::1]:25565
            out.append('[').append(ipAddress.getHostAddress()).append(']');
        } else {
            // e.g. 127.0.0.1:25565
            out.append(ipAddress.getHostAddress());
        }
        out.append(':');
        out.append(socketAddress.getPort());
        return out.toString();
    }

    public GameServer(GlowServer server, CountDownLatch latch) {
        super(server, latch);
        bootstrap.childHandler(new GlowChannelInitializer(this));
    }

    @Override
    public ChannelFuture bind(InetSocketAddress address) {
        ConsoleMessages.Info.Net.BINDING.log(formatAddress(address));
        return super.bind(address);
    }

    @Override
    public void onBindSuccess(InetSocketAddress address) {
        getServer().setPort(address.getPort());
        getServer().setIp(address.getHostString());
        ConsoleMessages.Info.Net.BOUND.log(formatAddress(address));
        super.onBindSuccess(address);
    }

    @Override
    public void onBindFailure(InetSocketAddress address, Throwable t) {
        logBindFailure(address, t);
        System.exit(1);
    }

    // Package-visible for testing.
    static void logBindFailure(InetSocketAddress address, Throwable t) {
        ConsoleMessages.Error.Net.BIND_FAILED.log(
                address.getAddress().getHostAddress(), address.getPort());
        if (t.getMessage().contains("Cannot assign requested address")) { // NON-NLS
            ConsoleMessages.Error.Net.CANNOT_ASSIGN.log(t);
        } else if (t.getMessage().contains("Address already in use")) { // NON-NLS
            ConsoleMessages.Error.Net.IN_USE.log(t);
        } else {
            ConsoleMessages.Error.Net.BIND_FAILED_UNKNOWN.log(t);
        }
    }

    @Override
    public GlowSession newSession(Channel c) {
        GlowSession session = new GlowSession(getServer(), c, this);
        getServer().getSessionRegistry().add(session);
        return session;
    }

    @Override
    public void sessionInactivated(Session session) {
        getServer().getSessionRegistry().remove((GlowSession) session);
    }
}

