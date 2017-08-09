package net.glowstone.net;

import com.flowpowered.network.ConnectionManager;
import com.flowpowered.network.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.glowstone.GlowServer;
import net.glowstone.net.pipeline.GlowChannelInitializer;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;


public final class GameServer extends GlowSocketServer implements ConnectionManager {
    public GameServer(GlowServer server, CountDownLatch latch) {
        super(server, latch);
        bootstrap.childHandler(new GlowChannelInitializer(this));
    }

    public ChannelFuture bind(InetSocketAddress address) {
        GlowServer.logger.info(GlowServer.lang.getString("server.bind.address", address));
        return super.bind(address);
    }

    @Override
    public void onBindSuccess(InetSocketAddress address) {
        getServer().setPort(address.getPort());
        getServer().setIp(address.getHostString());
        GlowServer.logger.info(GlowServer.lang.getString("server.bind.success", address));
        super.onBindSuccess(address);
    }

    @Override
    public void onBindFailure(InetSocketAddress address, Throwable t) {
        GlowServer.logger.severe(GlowServer.lang.getString("server.bind.failed", address));
        if (t.getMessage().contains("Cannot assign requested address")) {
            GlowServer.logger.severe(GlowServer.lang.getString("server.bind.invalid.1"));
            GlowServer.logger.severe(GlowServer.lang.getString("server.bind.invalid.2"));
            GlowServer.logger.severe(t.getLocalizedMessage());
        } else if (t.getMessage().contains("Address already in use")) {
            GlowServer.logger.severe(GlowServer.lang.getString("server.bind.taken.1"));
            GlowServer.logger.severe(GlowServer.lang.getString("server.bind.taken.2"));
            GlowServer.logger.severe(GlowServer.lang.getString("server.bind.taken.3"));
            GlowServer.logger.severe(t.getLocalizedMessage());
        } else {
            GlowServer.logger.log(Level.SEVERE, "An unknown bind error has occurred.", t);
        }
        System.exit(1);
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

