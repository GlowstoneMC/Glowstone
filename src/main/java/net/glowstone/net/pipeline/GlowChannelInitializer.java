package net.glowstone.net.pipeline;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.glowstone.net.GlowNetworkServer;
import net.glowstone.net.handler.legacyping.LegacyPingHandler;
import net.glowstone.net.protocol.ProtocolType;

/**
 * Used to initialize the channels.
 */
public final class GlowChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * The time in seconds which are elapsed before a client is disconnected due
     * to a read timeout.
     */
    private static final int READ_TIMEOUT = 20;

    /**
     * The time in seconds which are elapsed before a client is deemed idle due
     * to a write timeout.
     */
    private static final int WRITE_IDLE_TIMEOUT = 15;

    private final GlowNetworkServer connectionManager;

    public GlowChannelInitializer(GlowNetworkServer connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    protected void initChannel(SocketChannel c) {
        MessageHandler handler = new MessageHandler(connectionManager);
        CodecsHandler codecs = new CodecsHandler(ProtocolType.HANDSHAKE.getProtocol());
        FramingHandler framing = new FramingHandler();

        c.pipeline()
                .addLast("legacy_ping", new LegacyPingHandler(connectionManager))
                .addLast("encryption", NoopHandler.INSTANCE)
                .addLast("framing", framing)
                .addLast("compression", NoopHandler.INSTANCE)
                .addLast("codecs", codecs)
                .addLast("readtimeout", new ReadTimeoutHandler(READ_TIMEOUT))
                .addLast("writeidletimeout", new IdleStateHandler(0, WRITE_IDLE_TIMEOUT, 0))
                .addLast("handler", handler);
    }
}
