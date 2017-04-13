package net.glowstone.net.pipeline;

import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import net.glowstone.GlowServer;
import net.glowstone.net.GameServer;
import net.glowstone.net.handler.legacyping.LegacyPingHandler;
import net.glowstone.net.protocol.ProtocolType;

/**
 * Used to initialize the channels.
 */
public final class GlowChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final GameServer connectionManager;

    public GlowChannelInitializer(GameServer connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    protected void initChannel(SocketChannel c) {
        MessageHandler handler = new MessageHandler(connectionManager);
        CodecsHandler codecs = new CodecsHandler(ProtocolType.HANDSHAKE.getProtocol());
        FramingHandler framing = new FramingHandler();

        try {
            c.config().setOption(ChannelOption.IP_TOS, 0x18);
        } catch (ChannelException e) {
            // Not supported on all OSs, like Windows XP and lesser
            GlowServer.logger.warning("Your OS does not support type of service.");
        }

        c.pipeline()
                .addLast("legacy_ping", new LegacyPingHandler(connectionManager))
                .addLast("encryption", NoopHandler.INSTANCE)
                .addLast("framing", framing)
                .addLast("compression", NoopHandler.INSTANCE)
                .addLast("codecs", codecs)
                .addLast("handler", handler);
    }
}
